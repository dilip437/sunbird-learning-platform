package com.ilimi.taxonomy.controller;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.ekstep.common.dto.Response;
import org.ekstep.common.logger.PlatformLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ilimi.common.controller.BaseController;
import com.ilimi.taxonomy.mgr.IContentManager;

@Controller
@RequestMapping("/v1/library")
public class LibraryController extends BaseController {

    

    @Autowired
    private IContentManager contentManager;

    private String graphId = "domain";

    @RequestMapping(value = "/upload/{id:.+}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Response> upload(@PathVariable(value = "id") String id,
            @RequestParam(value = "file", required = true) MultipartFile file,
            @RequestHeader(value = "user-id") String userId) {
        String apiId = "library.upload";
        PlatformLogger.log("Upload | Id: " + id + " | File: " + file + " | user-id: " + userId);
        try {
            String name = FilenameUtils.getBaseName(file.getOriginalFilename()) + "_" + System.currentTimeMillis() + "."
                    + FilenameUtils.getExtension(file.getOriginalFilename());
            File uploadedFile = new File(name);
            file.transferTo(uploadedFile);
            Response response = contentManager.upload(id, "domain", uploadedFile, null);
            PlatformLogger.log("Upload | Response: " , response);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            PlatformLogger.log("Upload | Exception: " , e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }

    @RequestMapping(value = "/publish/{id:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Response> publish(@PathVariable(value = "id") String libraryId,
            @RequestHeader(value = "user-id") String userId) {
        String apiId = "library.publish";
        PlatformLogger.log("Publish library | Library Id : " , libraryId);
        try {
            Response response = contentManager.publish(graphId, libraryId, null);
            return getResponseEntity(response, apiId, null);
        } catch (Exception e) {
            PlatformLogger.log("Publish | Exception: " , e.getMessage(), e);
            return getExceptionResponseEntity(e, apiId, null);
        }
    }
}
