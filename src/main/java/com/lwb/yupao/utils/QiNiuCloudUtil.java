package com.lwb.yupao.utils;

import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.model.User;
import com.lwb.yupao.service.UserService;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
@Component
public class QiNiuCloudUtil {
    @Resource
    private UserService userService;
    private static final String QiNiuCloud_accessKey = "oAFI3uG9uflsbxqujN95n9_OXr8S27shdFTS3Jd1";
    private static final String QiNiuCloud_secretKey = "DA7CKU_Y6BlTrM2d2Thi9WbFQ_E0VN9g0jp--L5T";
    private static final String QiNiuCloud_bucket = "lwb214";
    private static final String QiNiuCloud_domainName = "http://cdn.ce182.com"; //七牛云的域名";
    private static final String customSuffix = ".png";//定义图片保存后的后缀

    /**
     * 上传图片到七牛云
     * @param file 图片
     * @return 返回图片存储后的新图片名
     */
    
    public  String uploadQiNiuCloudImage(MultipartFile file, HttpServletRequest request) throws Exception{
        if(file.isEmpty()) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }else if(file.getSize() > 1024*1024*10){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"图片大小不能超过10M");
        }
        //获取图片后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = null;
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        //允许上传的图片格式集合
        String[] suffixes = new String[]{".bmp", ".jpeg", ".jpg", ".png"};
        boolean bool = false;
        //判断格式是否在suffixes中
        for(String string : suffixes){
            if (string.equals(suffix)){
                bool = true;
                break;
            }
        }
        if(!bool){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"只允许上传bmp、jpeg、jpg、png格式的图片");
        }
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;//指定分片上传版本
        UploadManager uploadManager = new UploadManager(cfg);
        //生成上传凭证，然后准备上传
        User user = userService.getCurrentUser(request);
        String imageName = String.format("%s_%s",user.getUserAccount(),user.getCode());
        String userImageName = imageName + customSuffix;//图片保存到七牛云后的文件名

        try {
            byte[] uploadBytes = file.getBytes();
            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);
            Auth auth = Auth.create(QiNiuCloud_accessKey, QiNiuCloud_secretKey);
            String upToken = auth.uploadToken(QiNiuCloud_bucket, userImageName);

            try {
                uploadManager.put(byteInputStream,userImageName,upToken,null, null);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println("七牛云ERROR:" + r.toString());
                throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"上传失败");
            }
        } catch (UnsupportedEncodingException ex) {
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"上传失败");
        }
        return userImageName;
    }
    /**
     * 获取七牛云图片临时链接
     * @param fileName 图片名
     * @return 返回图片链接
     */
    public String getQiNiuCloudImageUrl(String fileName) {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        String publicUrl = String.format("%s/%s", QiNiuCloud_domainName, encodedFileName);

        Auth auth = Auth.create(QiNiuCloud_accessKey, QiNiuCloud_secretKey);
        long deadline = Long.MAX_VALUE;
        return auth.privateDownloadUrlWithDeadline(publicUrl,deadline);
    }
}
