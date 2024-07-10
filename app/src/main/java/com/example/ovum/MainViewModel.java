package com.example.ovum;
import static android.content.ContentValues.TAG;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.GlobalScope;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainViewModel extends ViewModel {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
    private final MutableLiveData<DayInfo> currentDate = new MutableLiveData<>();
    private final Flowable<PagingData<LocalDate>> flowablePagingData;

    private final Set<LocalDate> dueDates = new HashSet<>();
    private final Set<LocalDate> oneFromDueDates = new HashSet<>();
    private final Set<LocalDate> oneToDueDates = new HashSet<>();
    private final Set<LocalDate> twoFromDueDates = new HashSet<>();
    private final Set<LocalDate> twoToDueDates = new HashSet<>();

    public MainViewModel() {
        LocalDate today = LocalDate.now();
        DayInfo dayInfo = new DayInfo(
                today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                dateFormatter.format(today)
        );
        currentDate.setValue(dayInfo);

        Pager<Integer, LocalDate> pager = new Pager<>(
                new PagingConfig(
                        31, // Page size
                        31, // Prefetch distance
                        false // Enable placeholders
                ),
                () -> new HorizontalCalendarPagingSource()
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

    public void setDueDates(Set<LocalDate> dates) {
        dueDates.clear();
        dueDates.addAll(dates);
        Log.d(TAG, "Due dates set: " + dueDates);
    }

    public void setOneFromDueDates(Set<LocalDate> dates) {
        oneFromDueDates.clear();
        oneFromDueDates.addAll(dates);
    }

    public void setOneToDueDates(Set<LocalDate> dates) {
        oneToDueDates.clear();
        oneToDueDates.addAll(dates);
    }

    public void setTwoFromDueDates(Set<LocalDate> dates) {
        twoFromDueDates.clear();
        twoFromDueDates.addAll(dates);
    }

    public void setTwoToDueDates(Set<LocalDate> dates) {
        twoToDueDates.clear();
        twoToDueDates.addAll(dates);
    }

    public Set<LocalDate> getDueDates() {
        return dueDates;
    }

    public Set<LocalDate> getOneFromDueDates() {
        return oneFromDueDates;
    }

    public Set<LocalDate> getOneToDueDates() {
        return oneToDueDates;
    }

    public Set<LocalDate> getTwoFromDueDates() {
        return twoFromDueDates;
    }

    public Set<LocalDate> getTwoToDueDates() {
        return twoToDueDates;
    }

    public Flowable<PagingData<LocalDate>> getFlowablePagingData() {
        return flowablePagingData;
    }
}
