package com.example.whd.Connection;

import com.example.whd.Connection.Models.AvgBreakResponse;
import com.example.whd.Connection.Models.ComparisonResponse;
import com.example.whd.Connection.Models.EntryUpdate;
import com.example.whd.Connection.Models.ScheduleCreate;
import com.example.whd.Connection.Models.WorkEntryUpdateResponse;
import com.example.whd.Connection.Models.PasswordUpdate;
import com.example.whd.Connection.Models.ScheduleEntryResponse;
import com.example.whd.Connection.Models.ScheduleUpdate;
import com.example.whd.Connection.Models.ScheduleUpdateResponse;
import com.example.whd.Connection.Models.TokenResponse;
import com.example.whd.Connection.Models.TotalEntriesResponse;
import com.example.whd.Connection.Models.UserUpdate;
import com.example.whd.Connection.Models.UserUpdateResponse;
import com.example.whd.Connection.Models.WeeklyReportResponse;
import com.example.whd.Connection.Models.WorkCreate;
import com.example.whd.Connection.Models.WorkEntryResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // POST-Requests
    @FormUrlEncoded
    @POST("login")
    Call<TokenResponse> login (
            @Field("username") String username,
            @Field("password") String password
    );

    @POST("entries/")
    Call<WorkEntryResponse> createEntry (
            @Body WorkCreate entry
    );

    @POST("schedules/")
    Call<ScheduleEntryResponse> createSchedule (
            @Body ScheduleCreate entry
    );

    // GET-Requests
    @GET("entries/")
    Call<List<WorkEntryResponse>> getAllEntries(
    );

    @GET("schedules/")
    Call<List<ScheduleEntryResponse>> getAllSchedule (
    );

    @GET("entries/range/")
    Call<List<WorkEntryResponse>> getEntriesByRange (
            @Query("start") String startDate,
            @Query("end") String endDate
    );

    @GET("stats/weekly-report/")
    Call<List<WeeklyReportResponse>> getWeeklyReport (
            @Query("start") String startDate,
            @Query("end") String endDate
    );

    @GET("stats/total-entries/")
    Call<TotalEntriesResponse> getTotalEntries (
    );

    @GET("stats/avg-break/")
    Call<AvgBreakResponse> getAvgBreak (
    );

    @GET("stats/comparison/")
    Call<ComparisonResponse> getComparison (
            @Query("date") String date
    );

    // PUT-Requests
    @PUT("users/me/password")
    Call<String> changePassword (
            @Body PasswordUpdate passwordUpdate
    );

    @PUT("users/")
    Call<UserUpdateResponse> updateUsername (
            @Body UserUpdate userUpdate
    );

    @PUT("schedules/{schedule_id}")
    Call<ScheduleUpdateResponse> updateSchedule (
            @Path("schedule_id") int scheduleID,
            @Body ScheduleUpdate scheduleUpdate
    );

    @PUT("entries/{entry_id}")
    Call<WorkEntryUpdateResponse> updateEntry (
            @Path("entry_id") int entryID,
            @Body EntryUpdate entryUpdate
    );

    // DELETE-Requests
    @DELETE("schedules/{schedule_id}")
    Call<Void> deleteSchedule (
            @Path("schedule_id") int scheduleID
    );

    @DELETE("entries/{entry_id}")
    Call<Void> deleteEntry (
            @Path("entry_id") int entryID
    );
}