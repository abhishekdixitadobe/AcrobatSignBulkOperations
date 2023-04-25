package com.adobe.acrobatsign.model;

import java.util.Objects;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ReminderInfo
 */
public class ReminderInfo {
  @SerializedName("firstReminderDelay")
  private Integer firstReminderDelay = null;

  /**
   * The frequency at which reminder will be sent until the agreement is completed.&lt;br&gt;If frequency is not provided, the reminder will be sent once (if the agreement is available at the specified time) with the delay based on the firstReminderDelay field and will never repeat again. If the agreement is not available at that time, reminder will not be sent. Cannot be updated in a PUT
   */
  @JsonAdapter(FrequencyEnum.Adapter.class)
  public enum FrequencyEnum {
    DAILY_UNTIL_SIGNED("DAILY_UNTIL_SIGNED"),
    
    WEEKDAILY_UNTIL_SIGNED("WEEKDAILY_UNTIL_SIGNED"),
    
    EVERY_OTHER_DAY_UNTIL_SIGNED("EVERY_OTHER_DAY_UNTIL_SIGNED"),
    
    EVERY_THIRD_DAY_UNTIL_SIGNED("EVERY_THIRD_DAY_UNTIL_SIGNED"),
    
    EVERY_FIFTH_DAY_UNTIL_SIGNED("EVERY_FIFTH_DAY_UNTIL_SIGNED"),
    
    WEEKLY_UNTIL_SIGNED("WEEKLY_UNTIL_SIGNED"),
    
    ONCE("ONCE");

    private String value;

    FrequencyEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static FrequencyEnum fromValue(String text) {
      for (FrequencyEnum b : FrequencyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<FrequencyEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final FrequencyEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public FrequencyEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return FrequencyEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("frequency")
  private FrequencyEnum frequency = null;

  @SerializedName("lastSentDate")
  private Date lastSentDate = null;

  @SerializedName("nextSentDate")
  private Date nextSentDate = null;

  @SerializedName("note")
  private String note = null;

  @SerializedName("recipientParticipantIds")
  private List<String> recipientParticipantIds = null;

  @SerializedName("reminderId")
  private String reminderId = null;

  /**
   * Reminder can be sent based on when the agreement becomes available or when the reminder is created&lt;br&gt;AGREEMENT_AVAILABILITY: If the agreement is not available to the participant at the time of reminder creation, the reminder will be sent for the first time, only when the agreement becomes available to the participant taking the firstReminderDelay into account. Subsequent reminders will be sent based on the frequency specified.  If the agreement is already available to the participant at the time of reminder creation, the first reminder will be sent after the delay specified by firstReminderDelay from the reminder creation time.&lt;br&gt;REMINDER_CREATION: The first reminder will be sent after the delay specified by firstReminderDelay from the reminder creation time only if the agreement is available at that time. Subsequent reminders will be triggered based on the frequency specified and will be sent only if the agreement is available at that time.  For agreements in authoring state, creating reminder with startReminderCounterFrom as REMINDER_CREATION is not allowed.&lt;br&gt;Note : If firstReminderDelay, frequency and startReminderCounterFrom fields are not specified in POST, reminder will be sent right now if the agreement is available. If agreement is not available, an error will be thrown.  Cannot be updated in a PUT
   */
  @JsonAdapter(StartReminderCounterFromEnum.Adapter.class)
  public enum StartReminderCounterFromEnum {
    AGREEMENT_AVAILABILITY("AGREEMENT_AVAILABILITY"),
    
    REMINDER_CREATION("REMINDER_CREATION");

    private String value;

    StartReminderCounterFromEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static StartReminderCounterFromEnum fromValue(String text) {
      for (StartReminderCounterFromEnum b : StartReminderCounterFromEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<StartReminderCounterFromEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final StartReminderCounterFromEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public StartReminderCounterFromEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return StartReminderCounterFromEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("startReminderCounterFrom")
  private StartReminderCounterFromEnum startReminderCounterFrom = null;

  /**
   * Current status of the reminder.  The only valid update in a PUT is from ACTIVE to CANCELED.  Must be provided as ACTIVE in a POST.
   */
  @JsonAdapter(StatusEnum.Adapter.class)
  public enum StatusEnum {
    ACTIVE("ACTIVE"),
    
    CANCELED("CANCELED"),
    
    COMPLETE("COMPLETE");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    public static class Adapter extends TypeAdapter<StatusEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public StatusEnum read(final JsonReader jsonReader) throws IOException {
        String value = jsonReader.nextString();
        return StatusEnum.fromValue(String.valueOf(value));
      }
    }
  }

  @SerializedName("status")
  private StatusEnum status = null;

  public ReminderInfo firstReminderDelay(Integer firstReminderDelay) {
    this.firstReminderDelay = firstReminderDelay;
    return this;
  }

  public Integer getFirstReminderDelay() {
    return firstReminderDelay;
  }

  public void setFirstReminderDelay(Integer firstReminderDelay) {
    this.firstReminderDelay = firstReminderDelay;
  }

  public ReminderInfo frequency(FrequencyEnum frequency) {
    this.frequency = frequency;
    return this;
  }

   /**
   * The frequency at which reminder will be sent until the agreement is completed.&lt;br&gt;If frequency is not provided, the reminder will be sent once (if the agreement is available at the specified time) with the delay based on the firstReminderDelay field and will never repeat again. If the agreement is not available at that time, reminder will not be sent. Cannot be updated in a PUT
   * @return frequency
  **/
  public FrequencyEnum getFrequency() {
    return frequency;
  }

  public void setFrequency(FrequencyEnum frequency) {
    this.frequency = frequency;
  }

  public ReminderInfo lastSentDate(Date lastSentDate) {
    this.lastSentDate = lastSentDate;
    return this;
  }

   /**
   * The date when the reminder was last sent. Only provided in GET. Cannot be provided in POST request. If provided in POST, it will be ignored. Cannot be updated in a PUT. Format would be yyyy-MM-dd&#39;T&#39;HH:mm:ssZ. For example, e.g 2016-02-25T18:46:19Z represents UTC time
   * @return lastSentDate
  **/
  public Date getLastSentDate() {
    return lastSentDate;
  }

  public void setLastSentDate(Date lastSentDate) {
    this.lastSentDate = lastSentDate;
  }

  public ReminderInfo nextSentDate(Date nextSentDate) {
    this.nextSentDate = nextSentDate;
    return this;
  }

   /**
   * The date when the reminder is scheduled to be sent next. When provided in POST request, frequency needs to be ONCE (or not specified), startReminderCounterFrom needs to be REMINDER_CREATION (or not specified) and firstReminderDelay needs to be 0 (or not specified). Cannot be updated in a PUT. Format would be yyyy-MM-dd&#39;T&#39;HH:mm:ssZ. For example, e.g 2016-02-25T18:46:19Z represents UTC time
   * @return nextSentDate
  **/
  public Date getNextSentDate() {
    return nextSentDate;
  }

  public void setNextSentDate(Date nextSentDate) {
    this.nextSentDate = nextSentDate;
  }

  public ReminderInfo note(String note) {
    this.note = note;
    return this;
  }

   /**
   * An optional message sent to the recipients, describing why their participation is required
   * @return note
  **/
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public ReminderInfo recipientParticipantIds(List<String> recipientParticipantIds) {
    this.recipientParticipantIds = recipientParticipantIds;
    return this;
  }

  public ReminderInfo addRecipientParticipantIdsItem(String recipientParticipantIdsItem) {
    if (this.recipientParticipantIds == null) {
      this.recipientParticipantIds = new ArrayList<String>();
    }
    this.recipientParticipantIds.add(recipientParticipantIdsItem);
    return this;
  }

   /**
   * A list of one or more participant IDs that the reminder should be sent to. These must be recipients of the agreement and not sharees or cc&#39;s.
   * @return recipientParticipantIds
  **/
  public List<String> getRecipientParticipantIds() {
    return recipientParticipantIds;
  }

  public void setRecipientParticipantIds(List<String> recipientParticipantIds) {
    this.recipientParticipantIds = recipientParticipantIds;
  }

  public ReminderInfo reminderId(String reminderId) {
    this.reminderId = reminderId;
    return this;
  }

   /**
   * An identifier of the reminder resource created on the server. If provided in POST or PUT, it will be ignored
   * @return reminderId
  **/
  public String getReminderId() {
    return reminderId;
  }

  public void setReminderId(String reminderId) {
    this.reminderId = reminderId;
  }

  public ReminderInfo startReminderCounterFrom(StartReminderCounterFromEnum startReminderCounterFrom) {
    this.startReminderCounterFrom = startReminderCounterFrom;
    return this;
  }

   /**
   * Reminder can be sent based on when the agreement becomes available or when the reminder is created&lt;br&gt;AGREEMENT_AVAILABILITY: If the agreement is not available to the participant at the time of reminder creation, the reminder will be sent for the first time, only when the agreement becomes available to the participant taking the firstReminderDelay into account. Subsequent reminders will be sent based on the frequency specified.  If the agreement is already available to the participant at the time of reminder creation, the first reminder will be sent after the delay specified by firstReminderDelay from the reminder creation time.&lt;br&gt;REMINDER_CREATION: The first reminder will be sent after the delay specified by firstReminderDelay from the reminder creation time only if the agreement is available at that time. Subsequent reminders will be triggered based on the frequency specified and will be sent only if the agreement is available at that time.  For agreements in authoring state, creating reminder with startReminderCounterFrom as REMINDER_CREATION is not allowed.&lt;br&gt;Note : If firstReminderDelay, frequency and startReminderCounterFrom fields are not specified in POST, reminder will be sent right now if the agreement is available. If agreement is not available, an error will be thrown.  Cannot be updated in a PUT
   * @return startReminderCounterFrom
  **/
  public StartReminderCounterFromEnum getStartReminderCounterFrom() {
    return startReminderCounterFrom;
  }

  public void setStartReminderCounterFrom(StartReminderCounterFromEnum startReminderCounterFrom) {
    this.startReminderCounterFrom = startReminderCounterFrom;
  }

  public ReminderInfo status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Current status of the reminder.  The only valid update in a PUT is from ACTIVE to CANCELED.  Must be provided as ACTIVE in a POST.
   * @return status
  **/
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReminderInfo reminderInfo = (ReminderInfo) o;
    return Objects.equals(this.firstReminderDelay, reminderInfo.firstReminderDelay) &&
        Objects.equals(this.frequency, reminderInfo.frequency) &&
        Objects.equals(this.lastSentDate, reminderInfo.lastSentDate) &&
        Objects.equals(this.nextSentDate, reminderInfo.nextSentDate) &&
        Objects.equals(this.note, reminderInfo.note) &&
        Objects.equals(this.recipientParticipantIds, reminderInfo.recipientParticipantIds) &&
        Objects.equals(this.reminderId, reminderInfo.reminderId) &&
        Objects.equals(this.startReminderCounterFrom, reminderInfo.startReminderCounterFrom) &&
        Objects.equals(this.status, reminderInfo.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstReminderDelay, frequency, lastSentDate, nextSentDate, note, recipientParticipantIds, reminderId, startReminderCounterFrom, status);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReminderInfo {\n");
    
    sb.append("    firstReminderDelay: ").append(toIndentedString(firstReminderDelay)).append("\n");
    sb.append("    frequency: ").append(toIndentedString(frequency)).append("\n");
    sb.append("    lastSentDate: ").append(toIndentedString(lastSentDate)).append("\n");
    sb.append("    nextSentDate: ").append(toIndentedString(nextSentDate)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    recipientParticipantIds: ").append(toIndentedString(recipientParticipantIds)).append("\n");
    sb.append("    reminderId: ").append(toIndentedString(reminderId)).append("\n");
    sb.append("    startReminderCounterFrom: ").append(toIndentedString(startReminderCounterFrom)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
