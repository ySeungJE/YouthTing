package skuniv.capstone.domain.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FIleStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

//    public List<String> storeFiles(List<MultipartFile> multipartFiles) throws IOException { 프로필 한장만 저장하므로 아마 쓸일이 없을거
//        List<UploadFile> storeFileResult = new ArrayList<>();
//        for (MultipartFile multipartFile : multipartFiles) {
//            if (!multipartFile.isEmpty()) {
//                 storeFileResult.add(storeFile(multipartFile));
//            }
//        }
//        return storeFileResult;
//    }

    // 실제 파일을 경로에 저장하고 UploadFile 객체를 반환
    public String storeFile(MultipartFile profilePicture) throws IOException {
        if (profilePicture.isEmpty()) {
            return null;
        }
        String storeFileName = createStoreFileName(profilePicture.getOriginalFilename());
        profilePicture.transferTo(new File(getFullPath(storeFileName)));
        return storeFileName;
    }

    private String createStoreFileName(String originalFilename) {
        // 서버에 저장하는 파일명이기 때문에 유일한 고유이름을 생성
        String uuid = UUID.randomUUID().toString(); // "asd-qwed-vx-qwe-123f-asd"
        String ext = extractExt(originalFilename);
        return uuid + "." + ext; // "asd-qwed-vx-qwe-123f-asd.png"
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
