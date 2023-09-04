package uz.admiraldev.contacts.models;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contact implements Serializable {
    String firstName;
    String lastName = null;
    String email = null;
    String organization = null;
    String phoneNumber;
    String contactPhotoUri = null;
    boolean isMale = true;

    public Contact(String firstName, String phoneNumber, boolean isMale) {
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
        this.isMale = isMale;
    }

    public Contact(String firstName, String lastName, String email,
                   String organization, String phoneNumber,
                   String contactPhotoUri, boolean isMale) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organization = organization;
        this.phoneNumber = phoneNumber;
        this.contactPhotoUri = contactPhotoUri;
        this.isMale = isMale;
    }

    public String getContactPhotoUri() {
        return contactPhotoUri;
    }

    public void setContactPhotoUri(String contactPhotoUri) {
        this.contactPhotoUri = contactPhotoUri;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public static List<Contact> loadContacts() {
        List<Contact> contacts = new ArrayList<>();
        return contacts;

    }
}
