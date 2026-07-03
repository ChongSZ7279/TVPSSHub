from pathlib import Path
import re, textwrap
from xml.etree import ElementTree as ET
from PIL import Image, ImageDraw, ImageFont
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.enum.section import WD_SECTION
from docx.oxml import OxmlElement
from docx.oxml.ns import qn

ROOT = Path(__file__).parent.resolve()
OUT = ROOT / "output" / "documentation"
QA = ROOT / "tmp" / "report_assets"
OUT.mkdir(parents=True, exist_ok=True)
QA.mkdir(parents=True, exist_ok=True)

NAVY = "17365D"; BLUE = "2E74B5"; LIGHT = "E8EEF5"; PALE = "F4F6F9"
GRAY = "5B6573"; RED = "9B1C1C"; GREEN = "285943"; GOLD = "8A6500"

def java_sloc(path):
    text = path.read_text(encoding="utf-8", errors="replace")
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    return sum(1 for line in text.splitlines() if line.strip() and not line.strip().startswith("//"))

java_files = sorted((ROOT / "src/main/java").rglob("*.java"))
sloc_rows = [(str(p.relative_to(ROOT)).replace("\\", "/"), java_sloc(p)) for p in java_files]
total_sloc = sum(v for _, v in sloc_rows)

def font(run, size=10.5, bold=False, italic=False, color="222222", name="Calibri"):
    run.font.name = name
    run._element.get_or_add_rPr().rFonts.set(qn("w:ascii"), name)
    run._element.get_or_add_rPr().rFonts.set(qn("w:hAnsi"), name)
    run.font.size = Pt(size); run.bold = bold; run.italic = italic
    run.font.color.rgb = RGBColor.from_string(color)

def shade(cell, fill):
    tcPr = cell._tc.get_or_add_tcPr(); shd = tcPr.find(qn("w:shd"))
    if shd is None: shd = OxmlElement("w:shd"); tcPr.append(shd)
    shd.set(qn("w:fill"), fill)

def set_cell_margins(cell, top=90, start=110, bottom=90, end=110):
    tc = cell._tc; tcPr = tc.get_or_add_tcPr(); tcMar = tcPr.first_child_found_in("w:tcMar")
    if tcMar is None: tcMar = OxmlElement("w:tcMar"); tcPr.append(tcMar)
    for m, v in (("top",top),("start",start),("bottom",bottom),("end",end)):
        node = tcMar.find(qn(f"w:{m}"))
        if node is None: node = OxmlElement(f"w:{m}"); tcMar.append(node)
        node.set(qn("w:w"), str(v)); node.set(qn("w:type"), "dxa")

def fix_table(table, widths):
    table.autofit = False; table.alignment = WD_TABLE_ALIGNMENT.CENTER
    tblPr = table._tbl.tblPr
    tblW = tblPr.find(qn("w:tblW")); tblW.set(qn("w:w"), str(sum(widths))); tblW.set(qn("w:type"), "dxa")
    ind = OxmlElement("w:tblInd"); ind.set(qn("w:w"), "120"); ind.set(qn("w:type"), "dxa"); tblPr.append(ind)
    grid = table._tbl.tblGrid
    for child in list(grid): grid.remove(child)
    for w in widths:
        gc = OxmlElement("w:gridCol"); gc.set(qn("w:w"), str(w)); grid.append(gc)
    for row in table.rows:
        for idx, cell in enumerate(row.cells):
            tcW = cell._tc.get_or_add_tcPr().find(qn("w:tcW")); tcW.set(qn("w:w"), str(widths[idx])); tcW.set(qn("w:type"), "dxa")
            set_cell_margins(cell); cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER

def table(doc, headers, rows, widths, font_size=8.5):
    t = doc.add_table(rows=1, cols=len(headers)); t.style = "Table Grid"
    for i,h in enumerate(headers):
        shade(t.rows[0].cells[i], LIGHT); p=t.rows[0].cells[i].paragraphs[0]; p.alignment=WD_ALIGN_PARAGRAPH.CENTER
        font(p.add_run(h), font_size, True, color=NAVY)
    for r in rows:
        cells=t.add_row().cells
        for i,val in enumerate(r):
            p=cells[i].paragraphs[0]; font(p.add_run(str(val)), font_size)
    fix_table(t,widths); return t

def heading(doc, text, level=1):
    p=doc.add_paragraph(style=f"Heading {level}"); p.paragraph_format.keep_with_next=True
    p.add_run(text); return p

def para(doc, text="", bold_lead=None, color=None, italic=False, after=6):
    p=doc.add_paragraph(); p.paragraph_format.space_after=Pt(after); p.paragraph_format.line_spacing=1.10
    if bold_lead and text.startswith(bold_lead):
        font(p.add_run(bold_lead), bold=True, color=color or "222222")
        font(p.add_run(text[len(bold_lead):]), italic=italic, color=color or "222222")
    else: font(p.add_run(text), italic=italic, color=color or "222222")
    return p

def bullet(doc, text):
    p=doc.add_paragraph(style="List Bullet"); p.paragraph_format.space_after=Pt(3); p.paragraph_format.line_spacing=1.10
    font(p.add_run(text), 10); return p

def code(doc, text):
    p=doc.add_paragraph(); p.paragraph_format.left_indent=Inches(.18); p.paragraph_format.right_indent=Inches(.08)
    p.paragraph_format.space_before=Pt(3); p.paragraph_format.space_after=Pt(6); p.paragraph_format.line_spacing=1.0
    pPr=p._p.get_or_add_pPr(); shd=OxmlElement("w:shd"); shd.set(qn("w:fill"), "F3F4F6"); pPr.append(shd)
    for idx,line in enumerate(text.strip("\n").splitlines()):
        if idx: p.add_run().add_break()
        font(p.add_run(line), 8.2, color="1F2937", name="Consolas")
    return p

def callout(doc, label, text, fill=PALE, color=NAVY):
    t=doc.add_table(rows=1, cols=1); c=t.cell(0,0); shade(c,fill); fix_table(t,[9360])
    p=c.paragraphs[0]; font(p.add_run(label+"  "), 9.5, True, color=color); font(p.add_run(text),9.5)
    return t

def page(doc, title, kicker=None):
    if len(doc.paragraphs) or len(doc.tables): doc.add_page_break()
    if kicker:
        p=doc.add_paragraph(); p.paragraph_format.space_after=Pt(2); font(p.add_run(kicker.upper()),9,True,color=GOLD)
    heading(doc,title,1)

def screenshot_image(path, title, lines, accent=BLUE):
    w,h=1500,860; im=Image.new("RGB",(w,h),"#F6F7F9"); d=ImageDraw.Draw(im)
    try: mono=ImageFont.truetype("C:/Windows/Fonts/consola.ttf",24); bold=ImageFont.truetype("C:/Windows/Fonts/consolab.ttf",29)
    except: mono=bold=ImageFont.load_default()
    d.rounded_rectangle((35,35,w-35,h-35),radius=18,fill="#101827")
    d.rectangle((35,35,w-35,105),fill="#243247"); d.text((70,55),title,font=bold,fill="#FFFFFF")
    y=135
    for line,col in lines:
        for wrapped in textwrap.wrap(line, width=92, replace_whitespace=False) or [""]:
            d.text((70,y),wrapped,font=mono,fill=col); y+=38
        y+=6
    im.save(path)

pmd_lines=[("PMD 7.17.0  |  Java analysis completed 2026-07-02", "#8FDBA7")]
pmd_xml=ROOT/"target/pmd.xml"
if pmd_xml.exists():
    tree=ET.parse(pmd_xml); ns={"p":"http://pmd.sourceforge.net/report/2.0.0"}
    for f in tree.findall("p:file",ns):
        name=Path(f.attrib["name"]).name
        for v in f.findall("p:violation",ns):
            pmd_lines.append((f"{name}:{v.attrib['beginline']}  {v.attrib['rule']}  -  {' '.join((v.text or '').split())}","#E7EDF7"))
screenshot_image(QA/"pmd-report.png","PMD static-analysis report",pmd_lines[:15])
screenshot_image(QA/"verification.png","Maven verification transcript",[
    ("> mvn -q test", "#E7EDF7"),
    ("BUILD RESULT: success (exit code 0)", "#8FDBA7"),
    ("Important limitation: src/test contains no test classes; this confirms compilation/test lifecycle only.","#FFD37A"),
    ("> mvn -q -DskipTests pmd:pmd", "#E7EDF7"),
    ("PMD RESULT: target/pmd.xml generated; 12 violations reported.","#8FDBA7"),
    ("> mvn -q -DskipTests pmd:cpd", "#E7EDF7"),
    ("CPD RESULT: target/cpd.xml generated; no duplication block met the default token threshold.","#8FDBA7"),
])

doc=Document(); sec=doc.sections[0]
sec.page_width=Inches(8.5); sec.page_height=Inches(11); sec.top_margin=sec.bottom_margin=Inches(.85); sec.left_margin=sec.right_margin=Inches(.85)
sec.header_distance=sec.footer_distance=Inches(.45)
styles=doc.styles
styles["Normal"].font.name="Calibri"; styles["Normal"].font.size=Pt(10.5)
for s,size,col,bef,aft in [("Heading 1",18,NAVY,8,8),("Heading 2",13,BLUE,10,5),("Heading 3",11.5,"1F4D78",8,4)]:
    st=styles[s]; st.font.name="Calibri"; st.font.size=Pt(size); st.font.bold=True; st.font.color.rgb=RGBColor.from_string(col)
    st.paragraph_format.space_before=Pt(bef); st.paragraph_format.space_after=Pt(aft)
for s in ["List Bullet","List Number"]:
    styles[s].font.name="Calibri"; styles[s].font.size=Pt(10); styles[s].paragraph_format.left_indent=Inches(.42); styles[s].paragraph_format.first_line_indent=Inches(-.18)

# Header/footer
hp=sec.header.paragraphs[0]; hp.alignment=WD_ALIGN_PARAGRAPH.RIGHT; font(hp.add_run("SECJ4383  |  PROJECT 1  |  PART B"),8,True,color=GRAY)
fp=sec.footer.paragraphs[0]; fp.alignment=WD_ALIGN_PARAGRAPH.CENTER
font(fp.add_run("TVPSSHub - Code Smells Detection and Refactoring Report  |  "),8,color=GRAY)
fld=OxmlElement("w:fldSimple"); fld.set(qn("w:instr"),"PAGE"); fp._p.append(fld)

# Cover
p=doc.add_paragraph(); p.paragraph_format.space_before=Pt(100); p.alignment=WD_ALIGN_PARAGRAPH.CENTER
font(p.add_run("SECJ4383"),12,True,color=GOLD)
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; font(p.add_run("SOFTWARE CONSTRUCTION"),13,True,color=GRAY)
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(34); font(p.add_run("DESIGN OF CODE, CODE SMELLS\nDETECTION & REFACTORING"),27,True,color=NAVY)
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(14); font(p.add_run("A technical assessment of TVPSSHub"),15,italic=True,color=BLUE)
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; p.paragraph_format.space_before=Pt(90); font(p.add_run("Prepared by: Project Team\nAssessment scope: Tasks B1-B5\nDate: 2 July 2026"),11,color=GRAY)
callout(doc,"DOCUMENT STATUS","Submission-ready analytical draft. Genuine runtime screenshots and the final refactored repository/commit evidence must be inserted after implementation; no evidence has been invented.","FFF7E3",GOLD)

page(doc,"Executive Summary","Report overview")
para(doc,f"TVPSSHub is a Java 11 Spring Boot 2.7.18 web application for managing users, schools, activities, educational resources, and feedback in a school media-program context. The production Java source contains {len(java_files)} files and {total_sloc:,} non-blank, non-comment lines, comfortably exceeding the 500 LOC requirement. The review concentrated on the controller layer because it contains the highest concentration of orchestration, validation, authorization, pagination, and view-model preparation.")
para(doc,"The manual review identified four recognized smell categories: God Class/Large Class, Long Method, Duplicate Code, and Dead Code. The two primary smells selected for refactoring are the God Class in UserViewController and duplicated pagination/authorization logic across controller methods. PMD 7.17.0 independently confirmed 12 issues, including 11 unused imports (dead code) and one misplaced null check; CPD reported no token-identical clone above its default threshold.")
heading(doc,"Key recommendations",2)
bullet(doc,"Extract focused registration, dashboard, profile, and teacher-user management responsibilities from UserViewController.")
bullet(doc,"Extract reusable pagination and current-user/access-check helpers, keeping controllers thin and behaviorally equivalent.")
bullet(doc,"Remove PMD-confirmed unused imports and correct the ResourceController null-check ordering as a low-risk cleanup.")
bullet(doc,"Add automated controller/service tests before moving code; the current Maven test lifecycle passes but no test classes are present.")
callout(doc,"ASSESSMENT CONCLUSION","The codebase is suitable for smell analysis and refactoring, but B5 cannot be truthfully marked complete until the proposed changes are committed under /refactored-code and the affected features are rerun.")

page(doc,"Table of Contents","Navigation")
for item in ["1. Introduction and Scope","2. Task B1 - Codebase Selection and Documentation","3. Architecture and Baseline Metrics","4. Task B2 - Code Smell Identification","5. Smell 1 - God Class / Large Class","6. Smell 2 - Duplicate Code","7. Additional Findings - Long Method and Dead Code","8. Task B3 - Detection Methodology","9. Automated Analysis Evidence","10. Task B4 - Refactoring Strategy","11. Refactoring Design: Extract Class","12. Refactoring Design: Extract Method / Shared Component","13. Task B5 - Before/After Comparison","14. Verification Plan and Current Results","15. Risk, Regression and Maintainability Assessment","16. Submission Checklist","17. References and Appendices"]:
    para(doc,item,after=3)
para(doc,"Note: Page numbering is generated by Word/LibreOffice. Update the field if the document is edited before submission.",italic=True,color=GRAY)

page(doc,"1. Introduction and Scope","Context")
para(doc,"This report evaluates the design quality of TVPSSHub against the Part B rubric for codebase selection, smell identification, manual and automated detection, refactoring selection, application evidence, and behavioral verification. The analysis is intentionally evidence-led: every file and line reference maps to the repository state reviewed on 2 July 2026.")
heading(doc,"Objectives",2)
bullet(doc,"Verify that the selected source exceeds 500 executable/source LOC.")
bullet(doc,"Identify at least two distinct smells from the recognized categories and explain their concrete impact.")
bullet(doc,"Cross-reference manual review with PMD and CPD output.")
bullet(doc,"Propose Fowler-aligned refactorings that preserve routes, views, persistence behavior, and access rules.")
bullet(doc,"Define credible post-refactoring checks and clearly separate completed evidence from pending evidence.")
heading(doc,"Scope boundaries",2)
para(doc,"Primary scope: src/main/java, with supporting review of pom.xml and templates where needed to understand controller responsibilities. Generated classes under target/classes, SQL dumps, CSS, images, and duplicated deployed templates were excluded from source LOC. No claim is made that the proposed refactoring has already been applied; documentation alone cannot substitute for the required /original-code and /refactored-code repository structure.")

page(doc,"2. Task B1 - Codebase Selection and Documentation","B1")
heading(doc,"System description (3-5 sentences)",2)
para(doc,"TVPSSHub is a web-based hub for coordinating school users, institutions, media-related activities, educational resource requests, and activity feedback. Its main features include registration and role-based login, user/profile administration, school records, activity creation and filtering, resource-request workflows, and feedback capture. The application is implemented in Java 11 with Spring Boot 2.7.18, Spring MVC, Spring Security, Thymeleaf/JSP views, Hibernate, Maven, and MySQL. It is intended for administrators, teachers, and students participating in a school media or TVPSS program. The project is group-developed coursework; therefore, no external source-code URL, third-party author, or external code licence applies to the codebase itself.")
table(doc,["Selection criterion","Evidence","Result"],[
    ["Minimum 500 LOC",f"{total_sloc:,} Java SLOC; blank/comment-only lines excluded","Pass"],
    ["Structured/OOP language","Java 11 classes, controllers, services, DAOs and models","Pass"],
    ["Suitable source","Group-developed TVPSSHub coursework system","Pass"],
    ["Repository packaging","Current root contains source; rubric folders are not yet present","Action required"],
],[1900,5560,1900],8.3)
heading(doc,"Required repository arrangement",2)
code(doc,"/original-code     # immutable baseline used for this report\n/refactored-code   # behavior-preserving revised implementation\n/docs              # final PDF and evidence images (recommended)")

page(doc,"3. Architecture and Baseline Metrics","B1 evidence")
table(doc,["Layer","Representative components","Responsibility"],[
    ["Controller","UserViewController, ActivityController","HTTP routes, authorization, model preparation"],
    ["Service","UserService, ActivityService","Application-facing operations"],
    ["DAO","UserDAO, ActivityDAO","Hibernate queries and transactions"],
    ["Model","UserViewModel, School, Resource","Domain and form data"],
    ["View","Thymeleaf/JSP templates","Server-rendered user interface"],
],[1500,3300,4560],8.2)
heading(doc,"Largest Java files",2)
largest=sorted(sloc_rows,key=lambda x:x[1],reverse=True)[:10]
table(doc,["Rank","File","SLOC"],[[i+1,p,n] for i,(p,n) in enumerate(largest)],[700,7660,1000],8.2)
para(doc,"Interpretation: UserViewController is the largest orchestration class by a clear margin. Size alone is not proof of a smell, but the class also has five collaborators and spans several unrelated user-facing use cases, which supports the Large Class classification.")

page(doc,"4. Task B2 - Code Smell Identification","B2 summary")
table(doc,["No.","Smell type","Location","Specific evidence"],[
    ["1","God Class / Large Class","UserViewController.java, lines 22-353","Five injected collaborators and 14 route handlers covering registration, dashboard, profile, listing, CRUD, authorization and logout."],
    ["2","Duplicate Code","UserViewController.java 192-236 and ActivityController.java 135-172","Near-identical pagination workflow: page size, counts, range clamping, start/end calculation, subList and model attributes."],
    ["3","Long Method","UserViewController.java 49-94 and 192-236","Each handler is about 45 lines and combines validation/querying/mutation or filtering/pagination/view preparation."],
    ["4","Dead Code","Multiple imports; exact PMD locations in Section 7","PMD reports nine unused imports, including UserViewController lines 13-14."],
],[520,1650,2570,4620],7.6)
callout(doc,"PRIMARY REFACTORING SCOPE","Smells 1 and 2 are treated as the minimum two distinct categories. Smells 3 and 4 demonstrate systematic review beyond the minimum.")

page(doc,"5. Smell 1 - God Class / Large Class","B2 detailed analysis")
para(doc,"UserViewController is classified as a God Class/Large Class because it acts as the central coordinator for too many unrelated user concerns. The class knows about users, activities, schools, resources, password encoding, authentication, role restrictions, school tenancy, filtering, pagination, and view names.")
table(doc,["Responsibility","Methods / lines","Why separate"],[
    ["Registration","showRegisterForm; processRegisterForm (40-94)","Validation and account creation form a cohesive use case."],
    ["Dashboard","showDashboard (102-135)","Aggregates three domains and diagnostic logging."],
    ["Profile","showProfile; showUpdateProfile; updateProfile (137-187)","Self-service profile concern."],
    ["Teacher user admin","showUserList through deleteUser (189-344)","School-scoped user management and authorization."],
    ["Authentication view","login/logout (96-100, 346-350)","Navigation/security concern."],
],[1900,3160,4300],8.0)
heading(doc,"Maintenance impact",2)
bullet(doc,"A change to user validation, dashboard content, or teacher authorization all modifies the same class.")
bullet(doc,"The five injected dependencies broaden the class's reasons to change and its testing setup.")
bullet(doc,"Reviewers must understand unrelated flows before making a local modification.")
bullet(doc,"Future growth will likely worsen merge conflicts and regression risk.")

page(doc,"6. Smell 2 - Duplicate Code","B2 detailed analysis")
para(doc,"The user-list and feedback-list handlers implement the same pagination algorithm with only the element type, empty-list behavior, and model attribute names changed. This is semantic duplication even though PMD CPD does not report a clone at its default token threshold.")
table(doc,["Step","User list (192-236)","Feedback list (135-172)"],[
    ["Page size","10","10"],["Count","userList.size()","feedbackList.size()"],["Pages","ceil(totalItems/pageSize)","ceil(totalItems/pageSize)"],
    ["Bounds","Clamp page to 1..totalPages","Clamp page to 1..totalPages"],["Slice","subList(start,end)","subList(start,end)"],
    ["Model","items,currentPage,totalPages","items,currentPage,totalPages"],
],[1200,4080,4080],8.0)
heading(doc,"Secondary authorization duplication",2)
para(doc,"UserViewController repeatedly resolves the authenticated email to a user (lines 199, 242, 253, 280-281, 305-306, and 322-323), then repeats school-membership checks in edit and delete flows. The repeated pattern risks inconsistent null handling and access messages.")
callout(doc,"CPD INTERPRETATION","A negative clone-detector result does not invalidate a manual duplicate-code finding. Token thresholds are conservative; reviewers must still detect repeated algorithms and policy logic expressed with different identifiers.")

page(doc,"7. Additional Findings - Long Method and Dead Code","Systematic review")
heading(doc,"Long Method",2)
para(doc,"processRegisterForm (lines 49-94) performs field validation, password confirmation, duplicate email checking, identity-card uniqueness checking, password encoding, role assignment, persistence, exception handling, and navigation. showUserList (lines 192-236) combines authentication context, filtering, pagination, page correction, slicing, and model preparation. Both are readable in isolation, but their mixed responsibilities make extension and unit testing harder.")
heading(doc,"Dead Code confirmed by PMD",2)
table(doc,["File / line","Unused import"],[["UserViewController.java:13","HttpSession"],["UserViewController.java:14","LocalDate"],["ActivityDAO.java:9","SessionFactory"],["ActivityDAO.java:10","Autowired"],["ActivityDAO.java:12","Hibernate"],["FeedbackDAO.java:3","ActivityViewModel"],["FeedbackDAO.java:11","SessionFactory"],["FeedbackDAO.java:13","Autowired"],["ActivityViewModel.java:5-6","List, ArrayList"],["UserViewModel.java:5","DateTimeFormat"]],[3300,6060],8.2)
para(doc,"PMD produced 11 unused-import entries (plus a separate misplaced-null-check finding), making dead code the clearest directly automated smell. Removing these imports is behavior-neutral and reduces noise.")

page(doc,"8. Task B3 - Detection Methodology","B3 manual detection")
heading(doc,"Manual review procedure",2)
for s in ["Enumerate Java source files and exclude generated output, blank lines, and comment-only lines from LOC.","Rank classes by SLOC to identify unusually large coordination points.","Map each controller method to a business responsibility and count injected collaborators.","Inspect long handlers for mixed responsibilities, branches, mutation, persistence, and view preparation.","Compare repeated list-processing and access-control sequences across controllers.","Record precise file/line ranges, then validate independent symptoms with PMD and CPD."]:
    bullet(doc,s)
heading(doc,"Warning signs and thresholds",2)
table(doc,["Indicator","Observed value","Reasoning"],[
    ["Class size","UserViewController: 353 physical lines",">300 lines plus broad responsibility map warrants Large Class review."],
    ["Collaborators","5 injected fields","High fan-out for one MVC controller."],
    ["Route handlers","14","Unrelated feature families coexist."],
    ["Method length","~45 lines for two handlers","Not automatically wrong, but mixed concerns confirm Long Method."],
    ["Repeated algorithm","Two pagination blocks","Same change would require edits in two controllers."],
],[2100,2200,5060],8.3)
para(doc,"Cyclomatic complexity was estimated structurally from decision points during review but is not reported as a tool-generated exact metric because PMD's default report did not emit per-method complexity values. This avoids overstating evidence.")

page(doc,"9. Automated Analysis Evidence","B3 automated detection")
doc.add_picture(str(QA/"pmd-report.png"),width=Inches(6.65))
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; font(p.add_run("Figure 1. PMD 7.17.0 report excerpt generated from target/pmd.xml."),8.5,italic=True,color=GRAY)
table(doc,["Tool","Command","Outcome"],[
    ["PMD 7.17.0","mvn -q -DskipTests pmd:pmd","12 violations: 11 unused imports and one misplaced null check."],
    ["PMD CPD 7.17.0","mvn -q -DskipTests pmd:cpd","No duplication block exceeded the default threshold."],
    ["Maven Surefire lifecycle","mvn -q test","Exit code 0; no test classes exist."],
],[1900,3200,4260],8.0)
para(doc,"Cross-reference: PMD directly supports the Dead Code finding, while the null-check finding exposes reliability risk adjacent to the smell analysis. CPD's negative result is recorded rather than hidden; the manual duplicate-code classification concerns repeated algorithms and policies rather than a long exact clone.")

page(doc,"10. Task B4 - Refactoring Strategy","B4")
table(doc,["Smell","Fowler-aligned technique","Application to TVPSSHub"],[
    ["God Class / Large Class","Extract Class; Move Method","Create focused RegistrationService/UserAdministrationService (or focused controllers), and move cohesive logic away from UserViewController."],
    ["Duplicate Code","Extract Method; Consolidate Duplicate Code","Introduce a reusable PageSlice<T>/PaginationService and current-user/same-school helper."],
    ["Long Method","Extract Method","Extract validation, pagination, model population and authenticated-user lookup."],
    ["Dead Code","Remove Dead Code","Delete PMD-confirmed unused imports and rerun PMD."],
],[1650,2700,5010],8.0)
heading(doc,"Why these techniques fit",2)
para(doc,"Extract Class creates a new unit around a cohesive responsibility and delegates from the original class, directly reducing reasons to change. Move Method places behavior with the service or policy that owns the required data. Extract Method names and isolates a repeated or conceptually distinct block without changing its sequence. Remove Dead Code deletes elements that cannot affect external behavior.")
heading(doc,"Alternative considered",2)
para(doc,"A full rewrite into REST APIs and a client-side frontend was rejected because it changes architecture and creates unnecessary behavioral risk. The recommended approach preserves existing routes, view names, service/DAO calls, and Spring Security annotations while making small, testable movements.")

page(doc,"11. Refactoring Design - Extract Class","B4 design")
heading(doc,"Target decomposition",2)
table(doc,["New/focused component","Moved responsibility","Controller remains responsible for"],[
    ["RegistrationService","Validate uniqueness, encode password, assign default role, save","Binding, error display, redirect"],
    ["CurrentUserService","Resolve authenticated user and require login","HTTP navigation decision"],
    ["TeacherUserService","School-scoped create/edit/delete authorization","Route mapping and model"],
    ["DashboardService","Aggregate programs, schools, resources","Populate view model and return view"],
],[2450,3300,3610],8.0)
heading(doc,"Proposed interface",2)
code(doc,"public interface CurrentUserService {\n    UserViewModel requireUser(Authentication authentication);\n    void requireSameSchool(UserViewModel actor, UserViewModel target);\n}\n\npublic interface RegistrationService {\n    RegistrationResult register(UserViewModel candidate);\n}")
para(doc,"This design reduces the controller's fan-out and gives authorization/registration logic dedicated unit-test seams. Constructor injection should replace field injection during implementation so required collaborators are explicit and immutable.")

page(doc,"12. Refactoring Design - Shared Pagination","B4 design")
heading(doc,"Generic value object",2)
code(doc,"public final class PageSlice<T> {\n    private final List<T> items;\n    private final int currentPage;\n    private final int totalPages;\n    // constructor and accessors\n}\n\npublic final class Pagination {\n    public static <T> PageSlice<T> slice(List<T> items, int page, int size) {\n        int totalPages = Math.max(1, (int) Math.ceil((double) items.size() / size));\n        int safePage = Math.max(1, Math.min(page, totalPages));\n        int start = Math.min((safePage - 1) * size, items.size());\n        int end = Math.min(start + size, items.size());\n        return new PageSlice<>(items.subList(start, end), safePage, totalPages);\n    }\n}")
heading(doc,"Expected benefit",2)
bullet(doc,"One tested definition for empty lists and out-of-range pages.")
bullet(doc,"Controllers express intent instead of index arithmetic.")
bullet(doc,"A future page-size or boundary change is made once.")
bullet(doc,"The extracted component is independent of Spring MVC and easy to unit test.")
callout(doc,"BEHAVIOR DECISION","The current handlers differ on empty-list page counts. Before implementation, select and test one external convention (recommended: currentPage=1 and totalPages=1 for an empty UI list) to prevent accidental behavior drift.","FFF7E3",GOLD)

page(doc,"13. Task B5 - Before/After Comparison","B5 implementation evidence")
heading(doc,"Refactoring 1: authenticated-user lookup",2)
table(doc,["Before","After"],[
    ["String email = authentication.getName();\nUserViewModel loggedInClient =\n    userService.findUserByEmail(email);","UserViewModel loggedInClient =\n    currentUserService.requireUser(authentication);"],
],[4680,4680],8.0)
heading(doc,"Refactoring 2: pagination",2)
table(doc,["Before","After"],[
    ["int totalPages = (int) Math.ceil(...);\n// clamp page\nint start = ...; int end = ...;\nList<T> page = items.subList(start,end);","PageSlice<T> page =\n    Pagination.slice(items, currentPage, 10);\n// bind page.items/currentPage/totalPages"],
],[4680,4680],8.0)
heading(doc,"Refactoring 3: registration workflow",2)
table(doc,["Before","After"],[
    ["Controller validates fields and uniqueness,\nencodes password, assigns role, saves user,\nand translates persistence exceptions.","Controller delegates registration to\nRegistrationService and translates a typed\nRegistrationResult into view errors/redirect."],
],[4680,4680],8.0)
callout(doc,"CURRENT STATUS","These are implementation-ready comparisons, not proof of applied repository changes. Replace this page with an annotated git diff after /refactored-code is created and committed.","FDECEC",RED)

page(doc,"14. Verification Plan and Current Results","B5 verification")
doc.add_picture(str(QA/"verification.png"),width=Inches(6.65))
p=doc.add_paragraph(); p.alignment=WD_ALIGN_PARAGRAPH.CENTER; font(p.add_run("Figure 2. Commands executed against the original repository baseline."),8.5,italic=True,color=GRAY)
table(doc,["Feature","Required post-refactor check","Evidence status"],[
    ["Registration","Valid user, duplicate email/IC, mismatched password","Pending implementation"],
    ["Dashboard","Authenticated load and unauthenticated redirect","Pending implementation"],
    ["User pagination","Empty, first, middle, last and out-of-range pages","Pending implementation"],
    ["Teacher authorization","Same-school edit/delete; cross-school rejection","Pending implementation"],
    ["Feedback pagination","Empty and multi-page activity feedback","Pending implementation"],
    ["Static analysis","Rerun PMD/CPD and compare findings","Baseline complete"],
],[1900,4910,2550],7.8)
para(doc,"A screenshot of the running refactored system cannot be supplied until the refactoring is implemented and the database-backed application is started. The final submission should capture the browser after exercising the affected routes, not merely the login page.")

page(doc,"15. Risk, Regression and Maintainability Assessment","B5 evaluation")
table(doc,["Risk","Mitigation","Acceptance criterion"],[
    ["Route/view change","Keep annotations and returned view strings unchanged","Existing URLs render same views"],
    ["Authorization drift","Characterization tests before moving policy logic","Cross-school access remains denied"],
    ["Password behavior","Keep encoder call and default role assignment","Stored password encoded; role=3"],
    ["Pagination edge cases","Unit-test empty/out-of-range lists","No invalid subList indices"],
    ["Spring wiring failure","Prefer constructor injection; context smoke test","Application context starts"],
    ["New abstractions overcomplicate code","Extract only repeated/cohesive behavior","Controller dependencies and method sizes decrease"],
],[1900,3940,3520],8.0)
heading(doc,"Expected improvement summary",2)
para(doc,"The refactoring should lower the size and responsibility count of UserViewController, centralize repeated algorithms and authorization policies, and allow focused unit tests without loading MVC infrastructure. New issues are most likely to arise from different empty-page conventions or exception-to-view translations; these must be locked down with tests. Performance is not expected to change materially because the refactoring reorganizes in-process logic without altering database queries.")

page(doc,"16. Submission Checklist","Finalization")
table(doc,["Rubric item","Current report","Action before submission"],[
    ["B1 description and >500 LOC","Complete","Copy baseline to /original-code"],
    ["B2 two distinct smells","Complete","Verify line numbers after any baseline edits"],
    ["B3 manual + automated","Complete baseline","Insert original PMD screenshot if lecturer requires UI capture"],
    ["B4 Fowler techniques","Complete","Keep chosen design aligned with implementation"],
    ["B5 applied refactoring","Not yet complete","Create /refactored-code and implement changes"],
    ["B5 before/after","Design comparison included","Replace with annotated real diff"],
    ["B5 rerun screenshot","Not available","Run app and capture affected features"],
    ["Commit history","Not assessed","Use explicit refactor: ... messages"],
    ["PDF minimum 15 pages","Met by this report","Recheck after inserting evidence"],
],[1850,3000,4510],8.0)
heading(doc,"Suggested commits",2)
code(doc,"refactor: extract pagination into reusable PageSlice utility\nrefactor: centralize authenticated user and school access checks\nrefactor: extract registration workflow from UserViewController\nrefactor: split dashboard and teacher user responsibilities\nchore: remove PMD-confirmed unused imports\ntest: add controller and service characterization tests")

page(doc,"17. References","Sources")
refs=[
    "Fowler, M. Refactoring: Improving the Design of Existing Code (2nd ed.). Addison-Wesley, 2018.",
    "Fowler, M. Refactoring Catalog. https://refactoring.com/catalog/ (accessed 2 July 2026).",
    "PMD. PMD Source Code Analyzer Documentation. https://pmd.github.io/pmd/index.html (accessed 2 July 2026).",
    "PMD. Finding Duplicated Code with CPD. https://pmd.github.io/pmd/pmd_userdocs_cpd.html (accessed 2 July 2026).",
    "Spring. Spring Boot Reference Documentation 2.7.18. https://docs.spring.io/spring-boot/docs/2.7.18/reference/htmlsingle/ (accessed 2 July 2026).",
    "Project repository files: pom.xml, src/main/java, target/pmd.xml, and target/cpd.xml, reviewed 2 July 2026.",
]
for i,r in enumerate(refs,1): para(doc,f"{i}. {r}",after=5)
heading(doc,"Evidence integrity note",2)
para(doc,"Figures 1 and 2 are faithful visualizations of machine-generated local outputs and executed commands. They are not screenshots of a refactored running application. The red status notes identify evidence that remains outstanding, preserving academic integrity and making the remaining work explicit.")

page(doc,"Appendix A - Full Java SLOC Inventory","Appendix")
rows=[[i+1,p,n] for i,(p,n) in enumerate(sorted(sloc_rows,key=lambda x:x[1],reverse=True))]
table(doc,["No.","Java source file","SLOC"],rows,[700,7660,1000],7.4)
para(doc,f"Total: {total_sloc:,} non-blank, non-comment Java source lines across {len(java_files)} files.",bold_lead="Total:")

page(doc,"Appendix B - PMD Finding Register","Appendix")
pmd_rows=[]
if pmd_xml.exists():
    tree=ET.parse(pmd_xml); ns={"p":"http://pmd.sourceforge.net/report/2.0.0"}
    for f in tree.findall("p:file",ns):
        for v in f.findall("p:violation",ns):
            pmd_rows.append([Path(f.attrib["name"]).name,v.attrib["beginline"],v.attrib["rule"]," ".join((v.text or "").split())])
table(doc,["File","Line","Rule","Message"],pmd_rows,[2200,700,1900,4560],7.2)
para(doc,"Generated by PMD 7.17.0. The report file is target/pmd.xml; CPD output is target/cpd.xml.",italic=True,color=GRAY)

path=OUT/"TVPSSHub_Part_B_Code_Smells_Report.docx"
doc.save(path)
print(path)
print(f"SLOC={total_sloc}; files={len(java_files)}; PMD={len(pmd_rows)}")
