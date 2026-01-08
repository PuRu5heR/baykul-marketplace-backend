package by.baykulbackend.database.model;

public class Views {
    public interface UserView {
        interface Get {}
        interface Post {}
        interface Put {}
        interface Profile {}
    }

    public interface RefreshTokenView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface UserWithRefreshTokenView extends UserView.Get, RefreshTokenView.Get {}
}