package com.example.findprofessionals;

import android.content.Context;
import android.content.Intent;

public class StaticMethods {
    private static User user;

    private static boolean searchProfessionals;

    private static PageState ps = null;

    private static final String[] tags = {
            // Construction & Renovation
            "Construction",
            "Home Renovation",
            "Painting",
            "Plastering",
            "Tiling",
            "Flooring",
            "Roofing",
            "Carpentry",
            "Drywall Installation",
            "Concrete Work",
            "Waterproofing",
            "Demolition",

            // Electrical & Plumbing
            "Electrical Repair",
            "Electrical Installation",
            "Lighting Installation",
            "Smart Home Installation",
            "Plumbing",
            "Pipe Repair",
            "Drain Cleaning",
            "Water Heater Repair",
            "Air Conditioning",
            "Heating Systems",
            "Ventilation",

            // Cleaning & Maintenance
            "House Cleaning",
            "Deep Cleaning",
            "Office Cleaning",
            "Window Cleaning",
            "Carpet Cleaning",
            "Pool Cleaning",
            "Garden Maintenance",
            "Pest Control",
            "Furniture Assembly",

            // Technology & Computers
            "Computer Repair",
            "Laptop Repair",
            "Phone Repair",
            "Software Development",
            "Web Development",
            "Mobile App Development",
            "IT Support",
            "Network Installation",
            "Cybersecurity",
            "Data Recovery",
            "Database Management",
            "Computer Hardware",

            // Design & Media
            "Graphic Design",
            "Web Design",
            "UI/UX Design",
            "Logo Design",
            "Photography",
            "Videography",
            "Video Editing",
            "Animation",
            "3D Modeling",
            "Interior Design",
            "Architecture",

            // Automotive
            "Car Repair",
            "Car Maintenance",
            "Car Electrical Repair",
            "Car Detailing",
            "Car Washing",
            "Motorcycle Repair",
            "Tire Replacement",
            "Vehicle Diagnostics",

            // Education
            "Mathematics Tutoring",
            "Physics Tutoring",
            "Chemistry Tutoring",
            "English Tutoring",
            "Programming Tutoring",
            "Language Lessons",
            "Music Lessons",
            "Private Teaching",
            "Exam Preparation",

            // Personal Services
            "Hairdressing",
            "Barber",
            "Makeup Artist",
            "Nail Technician",
            "Massage Therapy",
            "Personal Training",
            "Fitness Coaching",
            "Nutrition Coaching",

            // Business & Professional
            "Accounting",
            "Bookkeeping",
            "Tax Consulting",
            "Legal Consulting",
            "Business Consulting",
            "Marketing",
            "Social Media Management",
            "Copywriting",
            "Translation",
            "Data Entry",

            // Events & Other
            "Event Planning",
            "Catering",
            "Cooking",
            "Baking",
            "Moving Services",
            "Delivery Services",
            "Pet Sitting",
            "Dog Walking",
            "Babysitting",
            "Tailoring",
            "Sewing",
            "Handyman Services"
    };

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        StaticMethods.user = user;
    }

    public static String[] getTags() {
        return tags;
    }

    public static boolean isSearchProfessionals() {
        return searchProfessionals;
    }

    public static void setSearchProfessionals(boolean searchProfessionals) {
        StaticMethods.searchProfessionals = searchProfessionals;
    }


    public static void InitPageState(Context context) {
        if (ps == null) {
            ps = new PageState(context.getApplicationContext());
        }
    }
    public static PageState getPs() {
        return ps;
    }

    public static Intent getIntel(Context context, String activity) {
        if (ps == null) {
            InitPageState(context);
        }

        Intent intent = null;

        if (activity != null) {
            switch (activity) {
                case "SearchActivity":
                    intent = new Intent(context, SearchActivity.class);
                    break;

                case "UserProfileActivity":
                    intent = new Intent(context, UserProfileActivity.class);
                    break;

                case "RequestActivity":
                    intent = new Intent(context, RequestActivity.class);
                    break;

                case "UpdateUserInfoActivity":
                    intent = new Intent(context, UpdateUserInfoActivity.class);
                    break;

                default: // NavActivity and default page
                    intent = new Intent(context, NavActivity.class);
                    break;
            }
        }

        return intent;
    }


}