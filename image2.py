import os
import re
import warnings
import logging
import cv2
import easyocr
import requests

warnings.filterwarnings("ignore", category=UserWarning)
logging.getLogger("easyocr").setLevel(logging.ERROR)

# OCR로 차량번호 인식 후 바로 차량 입차 처리
SERVER_URL = "http://localhost:8080/api/vehicle/entry"
BLE_ID = "YOUR_BLE_DEVICE_ID"

def filter_plate_text(text):
    return ''.join(
        ch for ch in text
        if ch.isdigit() or '\uAC00' <= ch <= '\uD7A3'
    )

def find_plate_text(results):
    for text in results:
        cleaned = filter_plate_text(text)

        if re.fullmatch(r'\d{2,3}[가-힣]\d{4}', cleaned):
            return cleaned

    return None


def resize_image(path):
    image = cv2.imread(path)

    if image is None:
        return None

    height, width = image.shape[:2]

    print(f"이미지 크기: {width} x {height}")

    if height < 200:
        scale = 200 / height

        image = cv2.resize(
            image,
            None,
            fx=scale,
            fy=scale,
            interpolation=cv2.INTER_CUBIC
        )

        new_height, new_width = image.shape[:2]

        print(
            f"이미지 확대: "
            f"{width} x {height} → {new_width} x {new_height}"
        )

    return image


def send_plate_to_server(plate_number):
    data = {
        "plateNumber": plate_number,
        "bleId": BLE_ID
    }

    try:
        response = requests.post(
            SERVER_URL,
            json=data,
            timeout=5
        )

        if response.ok:
            print("서버 전송 성공:", response.text)
        else:
            print(
                f"서버 전송 실패: "
                f"{response.status_code} {response.text}"
            )

    except requests.RequestException as e:
        print("서버 연결 오류:", e)

def main():
    path = input(
        "번호판 이미지 파일의 절대 경로를 입력해주세요: "
    ).strip()

    if not os.path.isfile(path):
        print("Error: 파일이 없습니다.")
        return

    image = resize_image(path)

    if image is None:
        print("Error: 이미지를 불러올 수 없습니다.")
        return

    reader = easyocr.Reader(
        ['ko', 'en'],
        gpu=False
    )

    results = reader.readtext(
        image,
        detail=0
    )

    if not results:
        print("번호판 문자를 인식하지 못했습니다.")
        return

    print("OCR 원본 결과:", results)

    plate_text = find_plate_text(results)

    if not plate_text:
        print("유효한 번호판 문자를 인식하지 못했습니다.")
        return

    print("인식된 번호판:", plate_text)

    send_plate_to_server(plate_text)

if __name__ == "__main__":
    main()
