package cutepush.beanorganized.user;

import cutepush.beanorganized.task.TaskEntity;

import java.util.List;

public class UserWithRelations {
    private final UserEntity user;
    private final UserProfileEntity profile;
    private final List<TaskEntity> tasks;

    public UserWithRelations(UserEntity user, UserProfileEntity profile, List<TaskEntity> tasks) {
        this.user = user;
        this.profile = profile;
        this.tasks = tasks;
    }

    public UserEntity getUser() {
        return user;
    }

    public UserProfileEntity getProfile() {
        return profile;
    }

    public List<TaskEntity> getTasks() {
        return tasks;
    }
}

