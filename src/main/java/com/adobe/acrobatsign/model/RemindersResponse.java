package com.adobe.acrobatsign.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

/**
 * RemindersResponse
 */

public class RemindersResponse {
  @SerializedName("reminderInfoList")
  private List<ReminderInfo> reminderInfoList = null;

  public RemindersResponse reminderInfoList(List<ReminderInfo> reminderInfoList) {
    this.reminderInfoList = reminderInfoList;
    return this;
  }

  public RemindersResponse addReminderInfoListItem(ReminderInfo reminderInfoListItem) {
    if (this.reminderInfoList == null) {
      this.reminderInfoList = new ArrayList<ReminderInfo>();
    }
    this.reminderInfoList.add(reminderInfoListItem);
    return this;
  }

   /**
   * A list of one or more reminders created on the agreement specified by the unique identifier agreementId by the user invoking the API.
   * @return reminderInfoList
  **/
  public List<ReminderInfo> getReminderInfoList() {
    return reminderInfoList;
  }

  public void setReminderInfoList(List<ReminderInfo> reminderInfoList) {
    this.reminderInfoList = reminderInfoList;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RemindersResponse remindersResponse = (RemindersResponse) o;
    return Objects.equals(this.reminderInfoList, remindersResponse.reminderInfoList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(reminderInfoList);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RemindersResponse {\n");
    
    sb.append("    reminderInfoList: ").append(toIndentedString(reminderInfoList)).append("\n");
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
