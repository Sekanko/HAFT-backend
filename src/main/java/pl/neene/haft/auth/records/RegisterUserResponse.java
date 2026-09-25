package pl.neene.haft.auth.records;

public record RegisterUserResponse(String message) {
    public static final RegisterUserResponse SUCCESS = new RegisterUserResponse("User registered successfully");
}
