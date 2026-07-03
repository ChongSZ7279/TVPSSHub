package com.example.model;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;

@Entity
@Table(name = "activity") // Make sure the table name matches your database
public class ActivityViewModel {
	@Id  // This marks the field as the primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generate the ID (if using auto-increment in DB)
	private int id;
	private String title;
	private String organizer;
	private String status;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate date;
	private String venue;
	private String district;
	private String targetLanguage;
	private String competitionLevel;
	private String description;
	private String fileUpload;
	private int programDuration;
	@Column(name = "creator_id")
	private long creatorId;  // New field for the creator's ID

	// Getters and Setters
	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	@Column(name = "participants_primary")
	private int participantsPrimary;

	@Column(name = "participants_secondary")
	private int participantsSecondary;

	@Column(name = "participants_open")
	private int participantsOpen;

	// Getters and setters
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}

	public void setFileUpload(String fileUpload) {
		this.fileUpload = fileUpload;
	}

	public String getFileUpload() {
		return fileUpload;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void setTargetLanguage(String targetLanguage) {
		this.targetLanguage = targetLanguage;
	}

	public String getCompetitionLevel() {
		return competitionLevel;
	}

	public void setCompetitionLevel(String competitionLevel) {
		this.competitionLevel = competitionLevel;
	}

	public int getProgramDuration() {
		return programDuration;
	}

	public void setProgramDuration(int programDuration) {
		this.programDuration = programDuration;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getParticipantsPrimary() {
		return participantsPrimary;
	}

	public void setParticipantsPrimary(int participantsPrimary) {
		this.participantsPrimary = participantsPrimary;
	}

	public int getParticipantsSecondary() {
		return participantsSecondary;
	}

	public void setParticipantsSecondary(int participantsSecondary) {
		this.participantsSecondary = participantsSecondary;
	}

	public int getParticipantsOpen() {
		return participantsOpen;
	}

	public void setParticipantsOpen(int participantsOpen) {
		this.participantsOpen = participantsOpen;
	}

	public void updateEditableFieldsFrom(ActivityViewModel source) {
		title = source.title;
		organizer = source.organizer;
		status = source.status;
		date = source.date;
		venue = source.venue;
		district = source.district;
		targetLanguage = source.targetLanguage;
		competitionLevel = source.competitionLevel;
		programDuration = source.programDuration;
		description = source.description;
		participantsPrimary = source.participantsPrimary;
		participantsSecondary = source.participantsSecondary;
		participantsOpen = source.participantsOpen;
	}

	@Override
	public String toString() {
		return "ActivityViewModel{" +
				"title='" + title + '\'' +
				", organizer='" + organizer + '\'' +
				", status='" + status + '\'' +
				", date=" + date +
				", venue='" + venue + '\'' +
				", district='" + district + '\'' +
				", targetLanguage='" + targetLanguage + '\'' +
				", competitionLevel='" + competitionLevel + '\'' +
				", programDuration=" + programDuration + '\'' +
				", description=" + description +
				'}';
	}
}
