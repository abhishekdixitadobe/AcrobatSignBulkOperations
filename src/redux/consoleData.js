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
  imgSrc: [],
  bulkActionType: null,
  apiResponse: [],
  swipperCards: [],
};

const consoleDataSlice = createSlice({
  name: "consoleData",
  initialState: initialState,
  reducers: {
    setConsoleImg(state, action) {
      state.imgSrc = [...state.imgSrc, action.payload];
    },
    deleteConsoleImgByIndex(state, action) {
      state.imgSrc = state.imgSrc.filter(
        (item, index) => index !== action.payload
      );
    },
    resetConsoleImg(state) {
      state.imgSrc = [];
    },
    setBulkActionType(state, action) {
      state.bulkActionType = action.payload;
    },
    setApiResponse(state, action) {
      state.apiResponse = action.payload;
    },
    setSwipperCards(state, action) {
      state.swipperCards = [...state.swipperCards, action.payload];
    },
    changeSwipperCardImage(state, action) {
      const { index, cover } = action.payload;
      if (state.swipperCards[index]) {
        state.swipperCards[index] = cover;
      }
    },
    resetState(state) {
      state.imgSrc = [];
      state.bulkActionType = null;
      state.apiResponse = [];
      state.swipperCards = [];
    },
  },
});

export const {
  setConsoleImg,
  setBulkActionType,
  setApiResponse,
  setSwipperCards,
  changeSwipperCardImage,
  deleteConsoleImgByIndex,
  resetConsoleImg,
  resetState,
} = consoleDataSlice.actions;

export default consoleDataSlice.reducer;
