import cv2
import os
import numpy as np

# 경로 설정
save_directory = 'face'
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
recognizer = cv2.face_LBPHFaceRecognizer.create()

def collect_data(target_label, max_count=200):
    cap = cv2.VideoCapture(0)
    count = 0
    faces = []
    labels = []

    while count < max_count:
        ret, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces_detected = face_cascade.detectMultiScale(gray, 1.3, 5)

        for (x, y, w, h) in faces_detected:
            face = gray[y:y+h, x:x+w]
            face = cv2.resize(face, (100, 100))
            faces.append(face)
            labels.append(target_label)
            count += 1

            # 현재 수집된 이미지의 개수를 화면에 표시
            cv2.putText(frame, f"Collected {count}/{max_count} images", (10, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
            # 검출된 얼굴 영역 표시
            cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)

        cv2.imshow("Collecting Faces", frame)

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
    cap.release()
    cv2.destroyAllWindows()
    return faces, labels

def train_model(faces, labels):
    recognizer.train(faces, np.array(labels))
    print("Model trained!")

def recognize_faces():
    cap = cv2.VideoCapture(0)
    while True:
        ret, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces_detected = face_cascade.detectMultiScale(gray, 1.3, 5)
        for (x, y, w, h) in faces_detected:
            face = gray[y:y+h, x:x+w]
            face = cv2.resize(face, (100, 100))
            label, confidence = recognizer.predict(face)
            if confidence < 100:
                confidence_percentage = 100 - confidence
                cv2.putText(frame, f"Approved {label} : {confidence_percentage:.2f}%", (x, y-10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)
            else:
                cv2.putText(frame, "Denied", (x, y-10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 0, 255), 2)
            cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)
        cv2.imshow("Face Recognition", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break
    cap.release()
    cv2.destroyAllWindows()

if __name__ == "__main__":
    # 첫 번째 특정인의 ID 입력
    target_label1 = int(input("Enter the ID for the first person: "))

    # 특정인의 얼굴 데이터 수집
    print("Collecting data for the first person. Press 'q' to stop.")
    faces_specific1, labels_specific1 = collect_data(target_label1, 200)

    # 두 번째 특정인의 ID 입력
    target_label2 = int(input("Enter the ID for the second person: "))

    # 특정인의 얼굴 데이터 수집
    print("Collecting data for the second person. Press 'q' to stop.")
    faces_specific2, labels_specific2 = collect_data(target_label2, 200)

    # 학습 데이터 합치기
    faces = faces_specific1 + faces_specific2
    labels = labels_specific1 + labels_specific2

    # 모델 학습
    print("Training the model...")
    train_model(faces, labels)

    # 예측 및 평가
    # 웹캠 시작
    # 실시간 얼굴 인식 및 승인
    print("Recognizing faces. Press 'q' to exit.")
    recognize_faces()
