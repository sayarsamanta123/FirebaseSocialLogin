package com.example.sayarsamanta.socialintegrationwithfirebase;
import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties
public class FormDetails {


        public String email,phone,location;

        // Default constructor required for calls to
        // DataSnapshot.getValue(User.class)
        public FormDetails() {
        }

    public FormDetails(String email, String phone, String location) {
        this.email = email;
        this.phone = phone;
        this.location = location;
    }
}
