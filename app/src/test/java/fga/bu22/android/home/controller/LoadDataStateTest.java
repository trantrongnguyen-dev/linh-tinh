package fga.bu22.android.home.controller;

import android.os.Message;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import fga.bu22.android.database.DatabaseHelper;
import fga.bu22.android.home.view.MainActivity;
import fga.bu22.android.models.Lesson;
import fga.bu22.android.models.TimeTable;
import fga.bu22.android.models.TimeTableModel;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by VuDuc on 4/25/2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({LoadDataState.class, MainActivity.class})
public class LoadDataStateTest {

    @Mock
    MainActivity mMockMainActivity;

    @Mock
    LoadDataState mMockLoadDataState;

    @Mock
    BaseState mMockBaseState;

    @Mock
    EditTimeTableController mMockEditTimeTableController;

    @Mock
    TimeTableModel mMockTimeTableModel;

    /**
     * Test for constructor_oneArgument.
     * Test condition: No condition
     * Expected result:
     * - Run method test success.
     */
    @Test
    public void LoadDataState_Constructor() {
        when(mMockEditTimeTableController.getMainActivity()).thenReturn(mMockMainActivity);
        LoadDataState loadDataState = new LoadDataState(mMockEditTimeTableController);
    }

    /**
     * Test for handleMsg_Case01.
     * Test condition:
     *  - msg.ojb is null
     * Expected result:
     * - Run method test success.
     * - Branch "false" of condition if (msg.obj != null) is executed.
     */
    @Test
    public void handleMsg_Case01() {
        Message mockMessage = Mockito.mock(Message.class);

        Whitebox.setInternalState(mockMessage, "obj", (Object[]) null);

        try {
            when(mMockLoadDataState, "handleMsg", mockMessage).thenCallRealMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMockLoadDataState.handleMsg(mockMessage);
    }

    /**
     * Test for handleMsg_Case02.
     * Test condition:
     *  - msg.ojb is not null
     * Expected result:
     * - Run method test success.
     * - Case default of condition switch (msg.obj.toString()) is executed.
     */
    @Test
    public void handleMsg_Case02() {
        Message mockMessage = Mockito.mock(Message.class);
        Object mockObject = Mockito.mock(Object.class);

        Whitebox.setInternalState(mockMessage, "obj", mockMessage);

        try {
            when(mMockLoadDataState, "handleMsg", mockMessage).thenCallRealMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMockLoadDataState.handleMsg(mockMessage);
    }

    /**
     * Test for handleMsg_Case03.
     * Test condition:
     * - msg.ojb = MainActivity.OBJ_LOAD_DATA_TIME_TABLE
     * Expected result:
     * - Run method test success.
     * - Case MainActivity.OBJ_LOAD_DATA_TIME_TABLE of condition switch (msg.obj.toString()) is executed.
     */
    @Test
    public void handleMsg_Case03() throws Exception {
        Message mockMessage = Mockito.mock(Message.class);
        DatabaseHelper databaseHelper = Mockito.mock(DatabaseHelper.class);
        List<TimeTable> mocktimeTables = Mockito.mock(List.class);

        TimeTable timeTable = new TimeTable();
        List<TimeTable> timeTableList = new ArrayList<>();

        Whitebox.setInternalState(mockMessage, "obj", MainActivity.OBJ_LOAD_DATA_TIME_TABLE);

        Whitebox.setInternalState(mMockLoadDataState, "mController", mMockEditTimeTableController);
        when(mMockEditTimeTableController.getMainActivity()).thenReturn(mMockMainActivity);
        when(mMockEditTimeTableController.getMainActivity().getWeek()).thenReturn(8);
        when(mMockEditTimeTableController.getMainActivity().getYear()).thenReturn(2018);

        when(mMockEditTimeTableController.getDatabaseHelper()).thenReturn(databaseHelper);
        when(mMockEditTimeTableController.getDatabaseHelper().getAllTimeTableByWeek(8, 2018)).thenReturn(timeTableList);

//        when(mocktimeTables.size()).thenReturn(3);
//        Whitebox.invokeMethod(mMockTimeTableModel, "setTimeTableList", timeTableList);
        try {
            when(mMockLoadDataState, "handleMsg", mockMessage).thenCallRealMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMockLoadDataState.handleMsg(mockMessage);
    }

    /**
     * Test for handleMsg_Case04.
     * Test condition:
     * - msg.ojb = MainActivity.OBJ_LOAD_DATA_LESSON
     * Expected result:
     * - Run method test success.
     * - Case MainActivity.OBJ_LOAD_DATA_LESSON of condition switch (msg.obj.toString()) is executed.
     */
    @Test
    public void handleMsg_Case04() {
        Message mockMessage = Mockito.mock(Message.class);
        DatabaseHelper databaseHelper = Mockito.mock(DatabaseHelper.class);

        List<Lesson> lessons = new ArrayList<>();

        Whitebox.setInternalState(mockMessage, "obj", MainActivity.OBJ_LOAD_DATA_LESSON);
        Whitebox.setInternalState(mMockLoadDataState, "mController", mMockEditTimeTableController);


        when(mMockEditTimeTableController.getDatabaseHelper()).thenReturn(databaseHelper);
        when(mMockEditTimeTableController.getDatabaseHelper().getAllLesson()).thenReturn(lessons);
        try {
            when(mMockLoadDataState, "handleMsg", mockMessage).thenCallRealMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMockLoadDataState.handleMsg(mockMessage);
    }
}