import Papa from 'papaparse';

// read CSV file and return array of objects
async function readCSV(file) {
  return new Promise((resolve, reject) => {
    Papa.parse(file, {
      header: true,
      complete: function(results) {
        // Remove the last line if it only contains empty strings
        if (Object.values(results.data[results.data.length - 1]).every(x => x === '')) {
          results.data.pop();
        }
        resolve(results.data);
      },
      error: function(err) {
        reject(err);
      }
    });
  });
}

export { readCSV };
