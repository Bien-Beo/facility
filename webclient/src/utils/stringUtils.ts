/**
 * Kiểm tra xem một chuỗi có chứa văn bản thực sự hay không
 * (không phải null, undefined, rỗng, hoặc chỉ chứa khoảng trắng).
 *
 * @param value Giá trị cần kiểm tra (có thể là string, null, hoặc undefined).
 * @returns True nếu chuỗi có nội dung văn bản, ngược lại trả về false.
 */
export function hasText(value: string | null | undefined): boolean {
    // 1. Kiểm tra null hoặc undefined trước tiên
    if (value === null || value === undefined) {
        return false;
    }

    // 2. Kiểm tra kiểu dữ liệu (đảm bảo là string) - An toàn hơn
    if (typeof value !== 'string') {
        // Hoặc bạn có thể ném lỗi ở đây nếu mong đợi chắc chắn là string
        return false;
    }

    // 3. Loại bỏ khoảng trắng ở đầu/cuối và kiểm tra độ dài
    return value.trim().length > 0;
}

// Bạn cũng có thể export dưới dạng class nếu thích, nhưng hàm đơn lẻ thường đủ dùng:
// export class StringUtils {
//     public static hasText(value: string | null | undefined): boolean {
//         if (value === null || value === undefined) { return false; }
//         if (typeof value !== 'string') { return false; }
//         return value.trim().length > 0;
//     }
// }