package io.amtech.projectflow.domain.project;

public enum ProjectStatus {
    UNAPPROVED,
    ON_PL_PLANNING,
    ON_DL_APPROVING,
    ON_DIRECTOR_APPROVING,
    DIRECTOR_APPROVED,
    DONE
}