/* ************************************************************************
 * ADOBE CONFIDENTIAL
 * ___________________
 *
 * Copyright 2024 Adobe
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains
 * the property of Adobe and its suppliers, if any. The intellectual
 * and technical concepts contained herein are proprietary to Adobe
 * and its suppliers and are protected by all applicable intellectual
 * property laws, including trade secret and copyright laws.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe.
 **************************************************************************/

import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  isDisabled: true,
  isConsoleOpen: true,
};

const consoleToggleSlice = createSlice({
  name: "consoleToggle",
  initialState: initialState,
  reducers: {
    setIsDisabled: (state) => {
      state.isDisabled = !state.isDisabled;
    },
    setIsConsoleOpen: (state) => {
      state.isConsoleOpen = !state.isConsoleOpen;
    },
    resetConsoleToggle: (state) => {
      state.isDisabled = true;
      state.isConsoleOpen = true;
    },
  },
});

export const { setIsDisabled, setIsConsoleOpen, resetConsoleToggle } =
  consoleToggleSlice.actions;

export default consoleToggleSlice.reducer;
