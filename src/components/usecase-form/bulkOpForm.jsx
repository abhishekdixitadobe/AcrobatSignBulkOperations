import { Flex, Image, Text, View } from "@adobe/react-spectrum";
import React, { useEffect, useState } from "react";
import DragAndDrop from "../drag-and-drop";
import RemoveBackgroundImg from "./images/removeBackground.png";
import MaskingImg from "./images/masking.png";
import AutoToneImg from "./images/autoTone.png";
import { set } from "lodash";
import { on } from "events";

const BulkOpForm = ({ onChange, setUploadFiles }) => {
  const [selectedOperation, setSelectedOperation] =
    useState("removeBackground");
  const [selectedFiles, setSelectedFiles] = useState([]);

  useEffect(() => {
    if (onChange) {
      onChange({
        operation: selectedOperation,
        files: selectedFiles,
      });
    }
  }, [selectedOperation, selectedFiles]);

  const operations = [
    {
      image: RemoveBackgroundImg,
      label: "Remove Background",
      value: "removeBackground",
    },
    { image: MaskingImg, label: "Create Mask", value: "createMask" },
    { image: AutoToneImg, label: "Auto Tone", value: "autoTone" },
  ];

  return (
    <>
      <Flex gap="size-100" direction={"column"}>
        <Flex direction={"row"} gap="size-100">
          {operations.map((operation, index) => (
            <div
              onClick={() => setSelectedOperation(operation.value)}
              key={index}
            >
              <View>
                <div
                  style={{
                    padding: "2px",
                    backgroundColor: "gray-100",
                    borderRadius: "10px",
                    border:
                      selectedOperation === operation.value
                        ? "2px solid var(--spectrum-global-color-gray-800)"
                        : "none",
                  }}
                >
                  <Image
                    src={operation.image}
                    objectFit="cover"
                    width="size-1200"
                    height="size-1200"
                    alt={operation.label}
                    UNSAFE_style={{
                      borderRadius: "8px",
                    }}
                  />
                </div>
                <Flex
                  direction="column"
                  alignItems="center"
                  justifyContent="center"
                  width={"size-1200"}
                >
                  <Text
                    UNSAFE_style={{
                      textAlign: "center",
                      wordWrap: "break-word",
                      fontSize: "12px",
                    }}
                  >
                    {operation.label}
                  </Text>
                </Flex>
              </View>
            </div>
          ))}
        </Flex>
        <View marginTop={"size-600"}>
          <DragAndDrop
            heading="Drag and drop images"
            description="Or, select multiple files from your computer"
            acceptedFileTypes={["image/jpeg", "image/png"]}
            allowMultiple
            onImageDrop={(file) => {
              setSelectedFiles(file);
            }}
            setUploadFiles={setUploadFiles}
          />
        </View>
      </Flex>
    </>
  );
};

export default BulkOpForm;
