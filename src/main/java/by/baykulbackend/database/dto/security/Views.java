package by.baykulbackend.database.dto.security;

public class Views {

    // User views
    public interface UserView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface UserFullView extends UserView.Get, RefreshTokenView.Get, BalanceView.Get {}

    // Refresh token views
    public interface RefreshTokenView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface RefreshTokenFullView extends RefreshTokenView.Get, UserView.Get {}

    // Balance views
    public interface BalanceView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface BalanceFullView extends BalanceView.Get, UserView.Get, BalanceHistoryView.Get {}

    // Balance history views
    public interface BalanceHistoryView {
        interface Get {}
        interface Post {}
        interface Put {}
    }

    public interface BalanceHistoryFullView extends BalanceHistoryView.Get, BalanceView.Get, UserView.Get {}

    // Part views
    public interface PartView {
        interface Get {}
        interface Post {}
        interface Put {}
    }
}

