import { Button, Text } from "@adobe/react-spectrum";
import PageGear from "@spectrum-icons/workflow/PageGear";
import React from "react";
import { useSelector } from "react-redux";
import { useDispatch } from "react-redux";
import { setIsConsoleOpen } from "../../redux/consoleToggle";

const Console = () => {
  const isDisabled = useSelector((state) => state.consoleToggle.isDisabled);
  const dispatch = useDispatch();

  const handleOpenConsole = () => {
    dispatch(setIsConsoleOpen(true));
  };

  return (
    <Button
      variant="primary"
      isDisabled={isDisabled}
      onPress={handleOpenConsole}
    >
      <PageGear />
      <Text>Console</Text>
    </Button>
  );
};

export default Console;
