import { combineReducers } from "redux";
import appReducer from "./app";
import apiCallreducer from "./apis";
import navStateReducer from "./navState";
import consoleDataReducer from "./consoleData";
import processApisReducer from "./processApis";
import consoleToggle from "./consoleToggle";
import logEventReducer from "./logEvent";
import downloadURLs from "./downloadURLs";
import authReducer from "./authReducer";

const rootReducer = combineReducers({
  app: appReducer,
  apis: apiCallreducer,
  navState: navStateReducer,
  consoleData: consoleDataReducer,
  processApis: processApisReducer,
  consoleToggle: consoleToggle,
  logEvent: logEventReducer,
  downloadURLs: downloadURLs,
  auth: authReducer,
});

export default rootReducer;
