package com.adobe.acrobatsign.model;

import java.io.IOException;
import java.util.Objects;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.annotations.ApiModelProperty;

public class DetailedUserInfo {
	public enum AccountTypeEnum {
		FREE("FREE"),

		PRO("PRO"),

		TEAM("TEAM"),

		TEAM_TRIAL("TEAM_TRIAL"),

		ENTERPRISE("ENTERPRISE"),

		ENTERPRISE_TRIAL("ENTERPRISE_TRIAL"),

		GLOBAL("GLOBAL"),

		GLOBAL_TRIAL("GLOBAL_TRIAL");

		public static class Adapter extends TypeAdapter<AccountTypeEnum> {
			@Override
			public AccountTypeEnum read(final JsonReader jsonReader) throws IOException {
				String value = jsonReader.nextString();
				return AccountTypeEnum.fromValue(String.valueOf(value));
			}

			@Override
			public void write(final JsonWriter jsonWriter, final AccountTypeEnum enumeration) throws IOException {
				jsonWriter.value(enumeration.getValue());
			}
		}

		public static AccountTypeEnum fromValue(String text) {
			for (AccountTypeEnum b : AccountTypeEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

		private String value;

		AccountTypeEnum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	/**
	 * Status of the user
	 */
	@JsonAdapter(StatusEnum.Adapter.class)
	public enum StatusEnum {
		ACTIVE("ACTIVE"),

		INACTIVE("INACTIVE"),

		CREATED("CREATED"),

		UNVERIFIED("UNVERIFIED");

		public static class Adapter extends TypeAdapter<StatusEnum> {
			@Override
			public StatusEnum read(final JsonReader jsonReader) throws IOException {
				String value = jsonReader.nextString();
				return StatusEnum.fromValue(String.valueOf(value));
			}

			@Override
			public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
				jsonWriter.value(enumeration.getValue());
			}
		}

		public static StatusEnum fromValue(String text) {
			for (StatusEnum b : StatusEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}

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
	}

	private AccountTypeEnum accountType = null;

	private String company = null;

	private String email = null;

	private String firstName = null;

	private String id = null;

	private String initials = null;

	private Boolean isAccountAdmin = null;

	private String lastName = null;

	private String locale = null;

	private String phone = null;

	private String accountId;

	@SerializedName("status")
	private StatusEnum status = null;

	@SerializedName("title")
	private String title = null;

	private String createdDate;

	public DetailedUserInfo accountType(AccountTypeEnum accountType) {
		this.accountType = accountType;
		return this;
	}

	public DetailedUserInfo company(String company) {
		this.company = company;
		return this;
	}

	public DetailedUserInfo email(String email) {
		this.email = email;
		return this;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}
		DetailedUserInfo detailedUserInfo = (DetailedUserInfo) o;
		return Objects.equals(this.accountType, detailedUserInfo.accountType)
				&& Objects.equals(this.company, detailedUserInfo.company)
				&& Objects.equals(this.email, detailedUserInfo.email)
				&& Objects.equals(this.firstName, detailedUserInfo.firstName)
				&& Objects.equals(this.id, detailedUserInfo.id)
				&& Objects.equals(this.initials, detailedUserInfo.initials)
				&& Objects.equals(this.isAccountAdmin, detailedUserInfo.isAccountAdmin)
				&& Objects.equals(this.lastName, detailedUserInfo.lastName)
				&& Objects.equals(this.locale, detailedUserInfo.locale)
				&& Objects.equals(this.phone, detailedUserInfo.phone)
				&& Objects.equals(this.status, detailedUserInfo.status)
				&& Objects.equals(this.title, detailedUserInfo.title);
	}

	public DetailedUserInfo firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	public String getAccountId() {
		return accountId;
	}

	/**
	 * Type of account to which the user belongs (null if no account)
	 *
	 * @return accountType
	 **/
	@ApiModelProperty(value = "Type of account to which the user belongs (null if no account)")
	public AccountTypeEnum getAccountType() {
		return accountType;
	}

	/**
	 * The name of company of the user
	 *
	 * @return company
	 **/
	@ApiModelProperty(value = "The name of company of the user")
	public String getCompany() {
		return company;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * The email address of the user
	 *
	 * @return email
	 **/
	@ApiModelProperty(value = "The email address of the user")
	public String getEmail() {
		return email;
	}

	/**
	 * The first name of the user
	 *
	 * @return firstName
	 **/
	@ApiModelProperty(value = "The first name of the user")
	public String getFirstName() {
		return firstName;
	}

	/**
	 * A unique identifier of the user resource for REST APIs as issued by Sign.
	 *
	 * @return id
	 **/
	@ApiModelProperty(value = "A unique identifier of the user resource for REST APIs as issued by Sign.")
	public String getId() {
		return id;
	}

	/**
	 * The initials of the user
	 *
	 * @return initials
	 **/
	@ApiModelProperty(value = "The initials of the user")
	public String getInitials() {
		return initials;
	}

	public Boolean getIsAccountAdmin() {
		return isAccountAdmin;
	}

	/**
	 * The last name of the user
	 *
	 * @return lastName
	 **/
	@ApiModelProperty(value = "The last name of the user")
	public String getLastName() {
		return lastName;
	}

	/**
	 * The UI locale of the user
	 *
	 * @return locale
	 **/
	@ApiModelProperty(value = "The UI locale of the user")
	public String getLocale() {
		return locale;
	}

	/**
	 * The phone number of the user
	 *
	 * @return phone
	 **/
	@ApiModelProperty(value = "The phone number of the user")
	public String getPhone() {
		return phone;
	}

	/**
	 * Status of the user
	 *
	 * @return status
	 **/
	@ApiModelProperty(value = "Status of the user")
	public StatusEnum getStatus() {
		return status;
	}

	/**
	 * The job title of the user
	 *
	 * @return title
	 **/
	@ApiModelProperty(value = "The job title of the user")
	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountType, company, email, firstName, id, initials, isAccountAdmin, lastName, locale,
				phone, status, title);
	}

	public DetailedUserInfo id(String id) {
		this.id = id;
		return this;
	}

	public DetailedUserInfo initials(String initials) {
		this.initials = initials;
		return this;
	}

	public DetailedUserInfo isAccountAdmin(Boolean isAccountAdmin) {
		this.isAccountAdmin = isAccountAdmin;
		return this;
	}

	/**
	 * True if the user is account admin
	 *
	 * @return isAccountAdmin
	 **/
	@ApiModelProperty(value = "True if the user is account admin")
	public Boolean isIsAccountAdmin() {
		return isAccountAdmin;
	}

	public DetailedUserInfo lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	public DetailedUserInfo locale(String locale) {
		this.locale = locale;
		return this;
	}

	public DetailedUserInfo phone(String phone) {
		this.phone = phone;
		return this;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public void setAccountType(AccountTypeEnum accountType) {
		this.accountType = accountType;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public void setIsAccountAdmin(Boolean isAccountAdmin) {
		this.isAccountAdmin = isAccountAdmin;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public DetailedUserInfo status(StatusEnum status) {
		this.status = status;
		return this;
	}

	public DetailedUserInfo title(String title) {
		this.title = title;
		return this;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class DetailedUserInfo {\n");

		sb.append("    accountType: ").append(toIndentedString(accountType)).append("\n");
		sb.append("    company: ").append(toIndentedString(company)).append("\n");
		sb.append("    email: ").append(toIndentedString(email)).append("\n");
		sb.append("    firstName: ").append(toIndentedString(firstName)).append("\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    initials: ").append(toIndentedString(initials)).append("\n");
		sb.append("    isAccountAdmin: ").append(toIndentedString(isAccountAdmin)).append("\n");
		sb.append("    lastName: ").append(toIndentedString(lastName)).append("\n");
		sb.append("    locale: ").append(toIndentedString(locale)).append("\n");
		sb.append("    phone: ").append(toIndentedString(phone)).append("\n");
		sb.append("    status: ").append(toIndentedString(status)).append("\n");
		sb.append("    title: ").append(toIndentedString(title)).append("\n");
		sb.append("}");
		return sb.toString();
	}

}
