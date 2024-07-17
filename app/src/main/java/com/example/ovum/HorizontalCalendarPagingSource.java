package com.example.ovum;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import kotlin.coroutines.Continuation;

public class HorizontalCalendarPagingSource extends PagingSource<Long, Day> {
    private final InstantProvider now;
    public List<Day> days;

    public HorizontalCalendarPagingSource(InstantProvider now) {
        this.now = now;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate getToday() {
        return now.now().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Object load(@NonNull LoadParams<Long> loadParams, @NonNull Continuation<? super LoadResult<Long, Day>> continuation) {
        try {
            long position = loadParams.getKey() != null ? loadParams.getKey() : 0;
            LocalDate today = getToday();
            LocalDate dueDate = getDatesList().get(0);
            LocalDate oneToDueDate = getDatesList().get(1);
            LocalDate twoToDueDate = getDatesList().get(2);
            LocalDate oneFromDueDate = getDatesList().get(3);
            LocalDate twoFromDueDate = getDatesList().get(4);

            days = new ArrayList<>();

            // Ensure today's date is within the range
            long startDay = position * 31L - 15;
            long endDay = position * 31L + 15;
            LocalDate startDate = today.plusDays(startDay);
            LocalDate endDate = today.plusDays(endDay);

            if (today.isBefore(startDate) || today.isAfter(endDate)) {
                if (today.isBefore(startDate)) {
                    startDate = today.minusDays(15);
                    endDate = startDate.plusDays(30);
                } else if (today.isAfter(endDate)) {
                    endDate = today.plusDays(15);
                    startDate = endDate.minusDays(30);
                }
            }

            for (long i = startDay; i <= endDay; i++) {
                LocalDate date = today.plusDays(i);
                Day day = new Day(
                        date.isEqual(today),
                        date.equals(dueDate),
                        date.equals(twoFromDueDate),
                        date.equals(oneFromDueDate),
                        date.equals(twoToDueDate),
                        date.equals(oneToDueDate),
                        date
                );
                days.add(day);
            }

            return new LoadResult.Page<>(
                    days,
                    position - 1,
                    position + 1
            );
        } catch (Exception e) {
            return new LoadResult.Error<>(e);
        }
    }

    @Nullable
    @Override
    public Long getRefreshKey(@NonNull PagingState<Long, Day> pagingState) {
        return null;
    }

    public interface InstantProvider {
        Instant now();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<LocalDate> getDatesList() {
        String dueDateString = "8-8-2024";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");
        LocalDate dueDate = LocalDate.parse(dueDateString, formatter);

        ArrayList<LocalDate> datesList = new ArrayList<>();
        datesList.add(dueDate);
        datesList.add(dueDate.minusDays(1));
        datesList.add(dueDate.minusDays(2));
        datesList.add(dueDate.plusDays(1));
        datesList.add(dueDate.plusDays(2));

        return datesList;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getPositionForDate(LocalDate date) {
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getDate().isEqual(date)) {
                return i;
            }
        }
        return -1;
    }
}
