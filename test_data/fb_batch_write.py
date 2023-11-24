import sys
import json
from datetime import datetime
from firebase_admin import credentials, initialize_app
from google.cloud import firestore


def import_data_to_firestore(db, test_user, data):
    collection_path = f"user/{test_user}/item"
    batch = db.batch()

    # Add each item to the batch under a unique document ID
    for item in data:
        document_id = db.collection(collection_path).document().id
        doc_ref = db.collection(collection_path).document(document_id)
        batch.set(doc_ref, item)

    # Commit the batch write
    batch.commit()
    print("Batch write completed.")


def create_user_if_not_exists(db, test_user):
    user_ref = db.collection("user").document(test_user)
    if not user_ref.get().exists:
        user_ref.set({})


def main(test_user, service_account_key_path, json_file_path):
    cred = credentials.Certificate(service_account_key_path)
    initialize_app(cred)
    db = firestore.Client.from_service_account_json(service_account_key_path)

    # Check if user document exists, create if not
    create_user_if_not_exists(db, test_user)

    # Read JSON file containg mock items
    with open(json_file_path, "r") as file:
        items = json.load(file)

    # Convert dates from string to Firestore Timestamps
    for item in items:
        item["acquisitionDate"] = datetime.strptime(item["acquisitionDate"], "%Y-%m-%d")

    import_data_to_firestore(db, test_user, items)


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print(
            """Usage: python3 fb_batch_write.py <username> <path/to/data.json> <path/to/service/account/key.json>
Arguments:
    <username>: The username of an existing Firestore user to batch write items to.
    <path/to/data.json>: Path to the JSON data file containing the items to be imported.
    <path/to/service/account/key.json>: Path to the Firebase service account key, providing write access to Firestore."""
        )
        sys.exit(1)

    test_user = sys.argv[1]
    json_file_path = sys.argv[2]
    service_account_key_path = sys.argv[3]

    main(test_user, service_account_key_path, json_file_path)
