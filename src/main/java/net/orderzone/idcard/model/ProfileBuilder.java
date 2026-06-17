package net.orderzone.idcard.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Factory that builds a Profile with sensible defaults.
 * Usage: ProfileBuilder.defaultStudent("Jane Doe", "Engineering").build()
 */
public class ProfileBuilder {

    private final Profile.ProfileBuilder inner;

    private ProfileBuilder(ProfileType type) {
        String uuid = UUID.randomUUID().toString();
        int year = LocalDate.now().getYear();
        String prefix = switch (type) {
            case STUDENT  -> "STU";
            case EMPLOYEE -> "EMP";
            case USER     -> "USR";
        };
        String regNumber = year + "-" + prefix + "-" + uuid.substring(0, 6).toUpperCase();

        inner = Profile.builder()
                .uuid(uuid)
                .registrationNumber(regNumber)
                .type(type)
                .issueDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(4))
                .barcodeType(BarcodeType.CODE_128);
    }

    public static ProfileBuilder defaultStudent(String fullName, String department) {
        return new ProfileBuilder(ProfileType.STUDENT)
                .name(fullName)
                .dept(department)
                .title("Student");
    }

    public static ProfileBuilder defaultEmployee(String fullName, String department, String jobTitle) {
        return new ProfileBuilder(ProfileType.EMPLOYEE)
                .name(fullName)
                .dept(department)
                .title(jobTitle);
    }

    public static ProfileBuilder defaultUser(String fullName) {
        return new ProfileBuilder(ProfileType.USER)
                .name(fullName);
    }

    private ProfileBuilder name(String v)  { inner.fullName(v);    return this; }
    private ProfileBuilder dept(String v)  { inner.department(v);  return this; }
    private ProfileBuilder title(String v) { inner.title(v);       return this; }

    public Profile build() { return inner.build(); }
}