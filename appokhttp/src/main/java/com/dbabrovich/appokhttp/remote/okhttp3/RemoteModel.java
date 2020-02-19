package com.dbabrovich.appokhttp.remote.okhttp3;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.immutables.value.Value;

import java.util.Date;
import java.util.List;

@Value.Immutable
@org.immutables.gson.Gson.TypeAdapters(emptyAsNulls = true)
public class RemoteModel {

    @Value.Immutable
    public interface JsonMatch {
        String id();

        Long feedMatchId();

        String homeTeamName();

        String homeTeamId();

        Long homeScore();

        String awayTeamName();

        String awayTeamId();

        Long awayScore();

        Long competitionId();

        String competition();

        @SerializedName("commentaryEntries")
        List<JsonComment> comments();
    }

    @Value.Immutable
    public interface JsonComment {
        String type();

        String comment();

        @Nullable
        String time();

        Long period();
    }

    @Value.Immutable
    public interface JsonMeta {
        Date createdAt();
    }

    @Value.Immutable
    public interface JsonCommentaryFeed {
        String status();

        JsonMeta metadata();

        @SerializedName("data")
        JsonMatch match();
    }
}
