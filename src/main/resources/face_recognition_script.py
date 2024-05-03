import face_recognition
import cv2
import sys

video_capture = cv2.VideoCapture('path_to_video.mp4')

known_image = face_recognition.load_image_file("/static/known_person.jpg")
known_face_encoding = face_recognition.face_encodings(known_image)[0]

known_faces = [known_face_encoding]

while video_capture.isOpened():
    ret, frame = video_capture.read()
    if not ret:
        print("End of video stream.")
        break

    # Найти все лица и характеристики лиц на текущем кадре видео
    face_locations = face_recognition.face_locations(frame)
    face_encodings = face_recognition.face_encodings(frame, face_locations)

    for face_encoding in face_encodings:
        matches = face_recognition.compare_faces(known_faces, face_encoding)
        if True in matches:
            print("Match found.")
            sys.stdout.flush()

video_capture.release()