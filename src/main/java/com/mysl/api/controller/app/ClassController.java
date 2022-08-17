package com.mysl.api.controller.app;

import cn.hutool.extra.cglib.CglibUtil;
import com.github.pagehelper.PageInfo;
import com.mysl.api.common.exception.ResourceNotFoundException;
import com.mysl.api.common.lang.ResponseData;
import com.mysl.api.config.security.JwtTokenUtil;
import com.mysl.api.entity.Class;
import com.mysl.api.entity.dto.*;
import com.mysl.api.service.ClassService;
import com.mysl.api.service.StoreService;
import com.mysl.api.service.StudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Su
 * @date 2022/8/9
 */
@Api(tags = "班级课程接口")
@Slf4j
@RestController
@RequestMapping("/app/classes")
@Secured("ROLE_STORE_MANAGER")
public class ClassController {

    @Autowired
    ClassService classService;
    @Autowired
    StudentService studentService;

    @ApiOperation("查询班级列表")
    @GetMapping
    public ResponseData<PageInfo<ClassDTO>> list(@ApiParam(value = "页数，默认 1")
                                                 @RequestParam(name = "page_num", defaultValue = "1", required = false) Integer pageNum,
                                                 @ApiParam(value = "每页记录，默认 20")
                                                 @RequestParam(name = "page_size", defaultValue = "20", required = false) Integer pageSize) {
        log.info("list class, pageNum: {}, pageSize: {}", pageNum, pageSize);

        return ResponseData.ok(classService.getClasses(pageNum, pageSize, JwtTokenUtil.getCurrentStoreId()));
    }

    @ApiOperation("创建班级")
    @PostMapping
    public ResponseData create(@Validated @RequestBody ClassCreateDTO dto) {
        Class entity = Class.builder().name(dto.getName()).teacher(dto.getTeacher())
                .storeId(JwtTokenUtil.getCurrentStoreId()).build();
        classService.save(entity);
        return ResponseData.ok();
    }

    @ApiOperation("查询班级详情")
    @GetMapping("/{id}")
    public ResponseData<ClassDTO> get(@PathVariable Long id) {
        Long storeId = JwtTokenUtil.getCurrentStoreId();
        if (storeId == null) {
            return ResponseData.ok();
        }
        List<ClassFullDTO> classFullDTOList = classService.getClasses(0, 1, id, storeId);
        if (CollectionUtils.isEmpty(classFullDTOList)) {
            throw new ResourceNotFoundException("找不到班级");
        }
        ClassDTO ret = new ClassDTO();
        BeanUtils.copyProperties(classFullDTOList.get(0), ret);
        return ResponseData.ok(ret);
    }

    @ApiOperation("查询班级学员列表")
    @GetMapping("/{class_id}/students")
    public ResponseData<List<StudentSimpleDTO>> getStudents(@PathVariable("class_id") Long classId) {
        List<StudentSimpleDTO> list = studentService.getSimpleStudents(JwtTokenUtil.getCurrentStoreId(), classId);
        return ResponseData.ok(list);
    }

    @ApiOperation("查询班级课程列表")
    @GetMapping("/{class_id}/courses")
    public ResponseData<List<CourseDTO>> getCourses(@PathVariable("class_id") Long classId) {
        return ResponseData.ok(classService.getClassCourse(JwtTokenUtil.getCurrentStoreId(), classId));
    }

    @ApiOperation("申请课程")
    @PostMapping("/{class_id}/courses/{id}/apply")
    public ResponseData apply(@PathVariable("class_id") Long classId, @PathVariable Long id) {
        classService.applyCourse(JwtTokenUtil.getCurrentStoreId(), classId, id);
        return ResponseData.ok();
    }

    @ApiOperation("查询课程内媒体列表")
    @GetMapping("/{class_id}/courses/{course_id}/media")
    public ResponseData<List<MediaDTO>> getMedia(@PathVariable("class_id") Long classId, @PathVariable("course_id") Long courseId) {
        return ResponseData.ok(classService.getClassCourseMedia(JwtTokenUtil.getCurrentStoreId(), classId, courseId));
    }

}
