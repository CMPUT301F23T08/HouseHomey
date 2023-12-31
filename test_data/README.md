# Firestore Batch Write Items Script

This script allows you to perform a batch write to a Firestore database using a provided JSON file in order to pre-populate a Firestore user with a HouseHomey item collection.

## Prerequisites

Before running the script, make sure you have the following:

### Firebase Service Account Key:

Two options for obtaining, either:

1. Ask @owencooke for the existing project key
2. Follow the guide [here](https://clemfournier.medium.com/how-to-get-my-firebase-service-account-key-file-f0ec97a21620) to generate a Firebase service account key.

Add the key to the `test_data` directory

### Mock Items in `data.json`

Expected format is an array of objects, each containing the Firestore fields for a HouseHomey item. Mandatory fields are shown in the first object below:

```javascript
[
  {
    "description": "Laptop",
    "acquisitionDate": "2023-08-09",
    "cost": "1500.99",
  },
  {
    "description": "Phone",
    "acquisitionDate": "2023-08-09",
    "cost": "1300.00",
    "make": "Apple",
    "model": "5s",
    "comment": "Green colorway",
    "serialNumber: "7110923456789012",
    "tags": ["electronics", "work"]
  },
]
```

## Usage

Install the required Python packages:

```bash
pip install -r requirements.txt
```

Run the script with the following command:

```bash
python3 fb_batch_write.py <username> <path/to/data.json> <path/to/service/account/key.json>
```

where:

- `username`: The Firestore username of the user whose item collection will be batch written to.
- `path/to/data.json`: Path to the JSON file containing the items to be imported.
- `path/to/service/account/key.json`: Path to the Firebase service account key file, providing write access to Firestore.
