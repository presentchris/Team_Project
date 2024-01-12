# 프로젝트 : 1인 사진 수집하여 얼굴 인식하기 or 팀원 사진 수집하여 팀원 얼굴 인식하기

# 1. 얼굴 검출하여 얼굴 사진 수집하기 100장
# Haar Cascade Face Detection 사용 : 
# cv2.CascadeClassifier(cv2.data.haarcascades + ‘haarcascade_frontalface_default.xml’)
# 2. 100장의 사진을 학습 시키기
# model = cv2.face.LBPHFaceRecognizer_create()
# model.train()
# 3. 얼굴 인식해서 학습한 인물인지 확인하여 ‘Access G 나타내고 정확도 표시
# model.predict(face)




# 적용 가능 기술: 특정 색상 셔츠 이용하기, 특정 색상의 종이 이용하기
# 학생들은 모든 가능성을 자유롭게 생각하고 상대 팀이 본인이 무엇을 하려고 하는지 알아채는 것을 어렵게 만들 수 있습니다.
#your code here

# !pip install opencv-contrib-python --user
import os
import cv2
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
    
    # 이미 저장된 이미지 파일 중 가장 큰 번호 찾기
    existing_files = os.listdir(save_directory)
    existing_numbers = [int(file.split('_')[1].split('.')[0]) for file in existing_files if file.startswith('face_')]
    if existing_numbers:
        next_number = max(existing_numbers) + 1
    else:
        next_number = 1
    
    while count < max_count:
        ret, frame = cap.read()
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        faces_detected = face_cascade.detectMultiScale(gray, 1.3, 5)

        for (x, y, w, h) in faces_detected:
            face = gray[y:y+h, x:x+w]
            face = cv2.resize(face, (400, 400))
            faces.append(face)
            labels.append(target_label)
            count += 1
            
            # 현재 수집된 이미지의 개수를 화면에 표시
            cv2.putText(frame, f"Collected {count}/{max_count} images", (10, 30), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
            # 검출된 얼굴 영역 표시
            cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)
            
            # 얼굴 이미지를 파일로 저장
            filename = os.path.join(save_directory, f"face_{next_number}.jpg")
            cv2.imwrite(filename, face)
            next_number += 1
        
        cv2.imshow("Collecting Faces", frame)
        
        if cv2.waitKey(10) & 0xFF == ord('q'):
            break
    cap.release()
    cv2.destroyAllWindows()
    return faces, labels

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

# 세 번째 특정인의 ID 입력
target_label3 = int(input("Enter the ID for the second person: "))

# 특정인의 얼굴 데이터 수집
print("Collecting data for the second person. Press 'q' to stop.")
faces_specific3, labels_specific3 = collect_data(target_label3, 200)


# 학습 데이터 합치기
faces = faces_specific1 + faces_specific2 + faces_specific3
labels = labels_specific1 + labels_specific2 + labels_specific3

# 모델 학습
print("Training the model...")
recognizer.train(faces, np.array(labels))
print("Model trained!")

# 예측 및 평가
# 웹캠 시작
# 실시간 얼굴 인식 및 승인
print("Recognizing faces. Press 'q' to exit.")
cap = cv2.VideoCapture(0)
while True:
    ret, frame = cap.read()
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces_detected = face_cascade.detectMultiScale(gray, 1.3, 5)
    for (x, y, w, h) in faces_detected:
        face = gray[y:y+h, x:x+w]
        face = cv2.resize(face, (100, 100))
        label, confidence = recognizer.predict(face)
        
        if label == target_label1:
            confidence_percentage = 100 - confidence
            cv2.putText(frame, f"Approved {target_label1} : {confidence_percentage:.2f}%", (x, y-10), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)
        elif label == target_label2:
            confidence_percentage = 100 - confidence
            cv2.putText(frame, f"Approved {target_label2} : {confidence_percentage:.2f}%", (x, y-10), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)
        elif label == target_label3:
            confidence_percentage = 100 - confidence
            cv2.putText(frame, f"Approved {target_label3} : {confidence_percentage:.2f}%", (x, y-10), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 255, 0), 2)
        else:
            cv2.putText(frame, "Denied", (x, y-10), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, (0, 0, 255), 2)

        cv2.rectangle(frame, (x, y), (x+w, y+h), (255, 0, 0), 2)
    cv2.imshow("Face Recognition", frame)
    if cv2.waitKey(50) & 0xFF == ord('q'):
        break
cap.release()
cv2.destroyAllWindows()
