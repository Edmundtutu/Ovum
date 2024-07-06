package com.example.ovum;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import kotlin.coroutines.Continuation;

public class HorizontalCalendarPagingSource extends PagingSource<Integer, LocalDate> {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Object load(@NonNull LoadParams<Integer> loadParams, @NonNull Continuation<? super LoadResult<Integer, LocalDate>> continuation) {
        try {
            int position = loadParams.getKey() != null ? loadParams.getKey() : 0;
            LocalDate today = LocalDate.now();

            List<LocalDate> dates = new ArrayList<>();
            for (int i = -15; i <= 15; i++) {
                dates.add(today.plusDays(position * 31L + i));
            }

            return new LoadResult.Page<>(
                    dates,
                    position - 1,
                    position + 1
            );
        } catch (Exception e) {
            return new LoadResult.Error<>(e);
        }
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, LocalDate> pagingState) {
        return null;
    }


}
