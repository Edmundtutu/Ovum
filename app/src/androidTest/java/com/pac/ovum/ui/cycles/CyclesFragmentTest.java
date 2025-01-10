package com.pac.ovum.ui.cycles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.pac.ovum.data.repositories.MockCycleRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CyclesFragmentTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MockCycleRepository mockCycleRepository;

    @Before
    public void setUp() {
        mockCycleRepository = new MockCycleRepository();
    }

    @Test
    public void testCyclesFragmentDisplaysData() {
        FragmentScenario<CyclesFragment> scenario = FragmentScenario.launchInContainer(CyclesFragment.class);

        scenario.onFragment(fragment -> {
            // Set up the ViewModel with the mock repository
            CyclesViewModelFactory factory = new CyclesViewModelFactory(mockCycleRepository);
            CyclesViewModel viewModel = new ViewModelProvider(fragment, factory).get(CyclesViewModel.class);

            // Assuming you have a way to set the ViewModel in the fragment, like in onCreateView or onViewCreated
//            fragment.getViewModel().setViewModel(viewModel); // Adjust this line based on your actual implementation

            // Ensure that the fragment is in a valid state before performing actions
            assertNotNull(fragment.cyclesAdapter); // Check that the adapter is initialized

            // Optionally, you can check if the data is displayed correctly
            // For example, if you have a RecyclerView, you can check its item count
            assertEquals(3, fragment.cyclesAdapter.getItemCount()); // Assuming you expect 3 items
        });
    }
}