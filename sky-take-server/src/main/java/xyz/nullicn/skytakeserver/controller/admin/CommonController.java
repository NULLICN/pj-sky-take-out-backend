package xyz.nullicn.skytakeserver.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.nullicn.constant.MessageConstant;
import xyz.nullicn.result.Result;
import xyz.nullicn.utils.AliOssUtil;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Validated
@RequiredArgsConstructor
@Tag(name = "通用接口", description = "文件上传")
public class CommonController {

    private final AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传: {}", file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            // String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(objectName); // todo 临时返回一个假的文件路径，后续oss问题解决再补上
        } catch (Exception e) {
            log.error("文件上传失败: {}", e);
        }

        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
