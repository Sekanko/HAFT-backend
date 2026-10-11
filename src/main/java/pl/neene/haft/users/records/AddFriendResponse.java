package pl.neene.haft.users.records;

public record AddFriendResponse(String message) {
    public static AddFriendResponse CREATED = new AddFriendResponse("Friendship request created successfully");
}
