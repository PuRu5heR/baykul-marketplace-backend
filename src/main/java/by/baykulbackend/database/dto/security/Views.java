package by.baykulbackend.database.dto.security;

public class Views {
    public interface UserView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface RefreshTokenView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface BalanceView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface BalanceHistoryView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface UserWithRefreshTokenView extends UserView.Get, RefreshTokenView.Get {}

    public interface BalanceWithUserView extends BalanceView.Get, UserView.Get {}

    public interface BalanceWithHistoryView extends BalanceView.Get, BalanceHistoryView.Get {}
}