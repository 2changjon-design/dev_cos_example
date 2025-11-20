package com.example.study.service;

import org.springframework.stereotype.Service;

@Service
public class LessonChecklistService {

    public boolean isEligible(int experienceYears, int completedProjects) {
        if (experienceYears < 0 || completedProjects < 0) {
            throw new IllegalArgumentException("Experience and projects must be non-negative.");
        }
        return experienceYears >= 2 && completedProjects >= 3;
    }

    public int estimatePracticeHours(int weeks) {
        if (weeks <= 0) {
            throw new IllegalArgumentException("Weeks must be positive.");
        }
        return weeks * 5;
    }
}
