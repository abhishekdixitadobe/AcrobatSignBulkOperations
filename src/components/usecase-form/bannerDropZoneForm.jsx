import React, { useEffect, useState } from "react";
import DragAndDrop from "../drag-and-drop";
import { Heading } from "@adobe/react-spectrum";
const BannerDropZoneForm = ({ action, onChange, setUploadFiles }) => {
  const [selectedFiles, setSelectedFiles] = useState([]);

  useEffect(() => {
    if (onChange) {
      const filesBlob = selectedFiles.map((file) => {
        return URL.createObjectURL(file);
      });
      onChange({ operation: "", files: selectedFiles });
    }
  }, [selectedFiles]);

  return (
    <>
      <Heading level={4} UNSAFE_style={{ marginBottom: "0px" }}>
        Upload Product Image
      </Heading>
      <DragAndDrop
        heading="Drag and drop images"
        description="Or, select multiple files from your computer"
        acceptedFileTypes={[
          "image/jpeg",
          "image/png",
          "image/vnd.adobe.photoshop",
          "image/x-photoshop",
          "image/psd",
          "application/photoshop",
          "application/psd",
          "zz-application/zz-winassoc-psd",
          "application/octet-stream",
        ]}
        allowMultiple
        onImageDrop={(file) => {
          setSelectedFiles(file);
        }}
        setUploadFiles={setUploadFiles}
      />
      <Heading level={4} UNSAFE_style={{ marginBottom: "0px" }}>
        Upload CSV
      </Heading>
      <DragAndDrop
        heading="Drag and drop CSV"
        description="Or, select single CSV file from your computer"
        acceptedFileTypes={["text/csv"]}
        onImageDrop={(files) => {
          setSelectedFiles((prevFiles) => [...prevFiles, ...files]);
        }}
      />
    </>
  );
};

export default BannerDropZoneForm;
