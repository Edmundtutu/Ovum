package com.example.ovum;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.GlobalScope;
@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends ViewModel {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("LLL d, yyyy");
    private final MutableLiveData<DayInfo> currentDate = new MutableLiveData<>();
    private final Flowable<PagingData<Day>> flowablePagingData;
    private MutableLiveData<Integer> _goToPosition = new MutableLiveData<>(-1);
    private LiveData<Integer> goToPosition = _goToPosition;
    private MutableLiveData<HorizontalCalendarPagingSource> horizontalCalendarSource = new MutableLiveData<>();

    public MainViewModel() {
        LocalDate today = LocalDate.now();
        DayInfo dayInfo = new DayInfo(
                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                dateFormatter.format(today)
        );
        currentDate.setValue(dayInfo);

        HorizontalCalendarPagingSource pagingSource = new HorizontalCalendarPagingSource(this::getCurrentInstant);
        horizontalCalendarSource.setValue(pagingSource);

        Pager<Long, Day> pager = new Pager<>(
                new PagingConfig(
                        31,
                        31,
                        false
                ),
                () -> pagingSource
        );

        flowablePagingData = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowablePagingData, GlobalScope.INSTANCE);
    }

    public LiveData<DayInfo> getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate date) {
        DayInfo dayInfo = new DayInfo(
                date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                dateFormatter.format(date)
        );
        currentDate.setValue(dayInfo);
    }

    public LiveData<Integer> getGoToPosition() {
        return goToPosition;
    }

    public LiveData<HorizontalCalendarPagingSource> getHorizontalCalendarSource() {
        return horizontalCalendarSource;
    }

    public void resetCalendarPosition() {
        HorizontalCalendarPagingSource source = horizontalCalendarSource.getValue();
        if (source != null) {
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            int position = source.getPositionForDate(today);
            Log.v("MainViewModel", "Resetting calendar position to " + position);
            if (position != -1) {
                _goToPosition.setValue(position);
            } else {
                Log.v("MainViewModel", "Date not found in HorizontalCalendarPagingSource");
            }
        } else {
            Log.v("MainViewModel", "HorizontalCalendarPagingSource is null");
        }
    }

    public Flowable<PagingData<Day>> getFlowablePagingData() {
        return flowablePagingData;
    }

    private Instant getCurrentInstant() {
        return Instant.now();
    }
}
