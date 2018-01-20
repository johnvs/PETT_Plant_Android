package com.biotronisis.pettplant.plant.processor;

import com.biotronisis.pettplant.communication.transfer.RequestStateResponse;
import com.biotronisis.pettplant.model.ColorMode;
import com.biotronisis.pettplant.model.Entrainment;

public class Plant {

    private static final String TAG = "Plant_Class";

    private final PlantState state;

    public Plant() {
        state = new PlantState();
    }

    public static boolean isValidState(RequestStateResponse response) {

        // Verify that the data of each element of the plant's state is in the correct range
        return (Entrainment.Sequence.isValid(response.getEntrainSequence().getId()) &&
              Entrainment.State.isValid(response.getEntrainmentState().getId()) &&
              Entrainment.LoopCheckbox.isValid(response.getLoopCheckbox().getId()) &&
              ColorMode.Mode.isValid(response.getColorMode().getId()) &&
              ColorMode.State.isValid(response.getColorModeState().getId()) &&
              ColorMode.Speed.isValid(response.getColorModeSpeed()));
    }

    public PlantState getState() {
        return state;
    }

    public void setState(RequestStateResponse response) {
        // Plant state data has been received from the plant and verified to be in the correct range.
        // Copy the data from the response object to the plant object
        state.setEntrainSequence(response.getEntrainSequence());
        state.setEntrainmentState(response.getEntrainmentState());
        state.setLoopCheckbox(response.getLoopCheckbox());

        state.setColorMode(response.getColorMode());
        state.setColorModeState(response.getColorModeState());
        state.setColorModeSpeed(response.getColorModeSpeed());
    }

}
