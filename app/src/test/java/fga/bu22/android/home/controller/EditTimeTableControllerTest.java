package fga.bu22.android.home.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fga.bu22.android.home.view.MainActivity;
import fga.bu22.android.models.TimeTableModel;

import static org.junit.Assert.*;

/**
 * Created by VuDuc on 4/25/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({EditTimeTableController.class, MainActivity.class})
public class EditTimeTableControllerTest {

    @Mock
    TimeTableModel mMockTimeTableModel;

    @Test
    public void getMainActivity() throws Exception {
    }

    @Test
    public void sendMessage() throws Exception {
    }

    @Test
    public void getDatabaseHelper() throws Exception {
    }

}