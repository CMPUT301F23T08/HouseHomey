# Firestore Batch Write Items Script

This script allows you to perform a batch write to a Firestore database using a provided JSON file in order to pre-populate a Firestore user with a HouseHomey item collection.

## Prerequisites

Before running the script, make sure you have the following:

### Firebase Service Account Key:

Two options for obtaining, either:

1. Ask @owencooke for the existing project key
2. Follow the guide [here](https://clemfournier.medium.com/how-to-get-my-firebase-service-account-key-file-f0ec97a21620) to generate a Firebase service account key.

Add the key to the `test_data` directory

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
