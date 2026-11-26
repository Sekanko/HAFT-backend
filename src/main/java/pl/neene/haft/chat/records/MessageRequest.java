package pl.neene.haft.chat.records;

import org.hibernate.validator.constraints.Length;


public record MessageRequest(
        @Length(max = 4000) String message
) {
}
