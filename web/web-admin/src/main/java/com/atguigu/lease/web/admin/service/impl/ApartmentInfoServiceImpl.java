package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.Result;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;

import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;//@MapperScan的路径扫描问题。

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    @Autowired
    private ApartmentLabelService apartmentLabelService;

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private ProvinceInfoMapper provinceInfoMapper;
    @Autowired
    private CityInfoMapper cityInfoMapper;
    @Autowired
    private DistrictInfoMapper districtInfoMapper;

    /**
     * 保存或更新公寓信息
     * @param apartmentSubmitVo
     */
    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        //根据vo对象的id区分，有id是修改，无id是新增
        boolean isUpdate = apartmentSubmitVo.getId() != null;

        //bug修改，前端是根据xxxname来展示地址的，所以新增时，虽然传入了地址id，但没有具体的name。
        //根据省份id查询省份name
        LambdaQueryWrapper<ProvinceInfo> provinceQueryWrapper = new LambdaQueryWrapper<>();
        provinceQueryWrapper.eq(ProvinceInfo::getId, apartmentSubmitVo.getProvinceId());//查询条件
        ProvinceInfo provinceInfo = provinceInfoMapper.selectOne(provinceQueryWrapper);
        apartmentSubmitVo.setProvinceName(provinceInfo.getName());
        //根据市id查询市name
        LambdaQueryWrapper<CityInfo> cityQueryWrapper = new LambdaQueryWrapper<>();
        cityQueryWrapper.eq(CityInfo::getId, apartmentSubmitVo.getCityId());//查询条件
        CityInfo cityInfo = cityInfoMapper.selectOne(cityQueryWrapper);
        apartmentSubmitVo.setCityName(cityInfo.getName());
        //根据地区id查询地区name
        LambdaQueryWrapper<DistrictInfo> districtQueryWrapper = new LambdaQueryWrapper<>();
        districtQueryWrapper.eq(DistrictInfo::getId, apartmentSubmitVo.getDistrictId());//查询条件
        DistrictInfo districtInfo = districtInfoMapper.selectOne(districtQueryWrapper);
        apartmentSubmitVo.setDistrictName(districtInfo.getName());


        super.saveOrUpdate(apartmentSubmitVo);//传入子类实例，即能保存或修改公寓信息表。
        //先移除相关信息，再添加信息。
        if (isUpdate) {
            //1.删除图片列表
            LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);//type
            graphInfoQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());//id
            graphInfoService.remove(graphInfoQueryWrapper);//删掉的是符合条件的所有记录。

            //2.删除配套列表
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentSubmitVo.getId());
            apartmentFacilityService.remove(facilityQueryWrapper);

            //3.删除标签列表
            LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            apartmentLabelService.remove(labelQueryWrapper);

            //4.删除杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
            feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(feeQueryWrapper);
        }
        //插入图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();//获取所有图片
        if (!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();//新建集合，将图片vo对象转成实体类对象
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();//图片实体类对象
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);//传入图片实体类集合，进行批量插入。
        }
        //插入配套列表
        List<Long> facilityInfoIdList = apartmentSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityInfoIdList)){
            ArrayList<ApartmentFacility> facilityList = new ArrayList<>();
            for (Long facilityId : facilityInfoIdList) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityId);
                facilityList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(facilityList);//批量插入
        }
        //插入标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            List<ApartmentLabel> apartmentLabelList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                apartmentLabelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabelList);//批量插入
        }
        //插入杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList);//批量插入
        }
    }

    /**
     * 根据条件分页查询公寓列表
     * @param page
     * @param queryVo
     * @return
     */
    @Override
    public IPage<ApartmentItemVo> pageApartmentItemByQuery(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        //将查询封装的逻辑分配到sql语句中，即mapper的实现中。
        //因为这里采用了page对象，所以只需要按照正常逻辑写sql就行，分页语句会自动加上。
        return apartmentInfoMapper.pageApartmentItemByQuery(page, queryVo);//直接传分页信息和地址信息。
    }

    /**
     * 根据id获取公寓详细信息
     * @return
     */
    @Override
    public ApartmentDetailVo getApartmentDetailById(Long id) {
        //1.查询apartmentInfo
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        if(apartmentInfo == null){
            return null;
        }
        //2.查询GraphInfo
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);//更加通用。
        //3.查询LabelInfo
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);
        //4.查询FacilityInfo
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);
        //5.查询FeeValue
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);
        //6.拼接ApartmentDetailVo对象并返回
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);//能够直接复制同名属性的工具方法，甚至可以没有继承关系。
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return apartmentDetailVo;
    }

    /**
     * 根据id删除公寓信息
     * @param id
     */
    @Override
    public void removeApartmentById(Long id) {
        super.removeById(id);//mybatis plus在service提供了直接操作的方法，无须显式定义mapper接口。
        //由于公寓下会包含房间信息，因此在删除公寓时最好先判断一下该公寓下是否存在房间信息，若存在，则提醒用户先删除房间信息后再删除公寓信息
        LambdaQueryWrapper<RoomInfo> roomInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoLambdaQueryWrapper.eq(RoomInfo::getApartmentId, id);
        Long count = roomInfoMapper.selectCount(roomInfoLambdaQueryWrapper);
        if (count > 0) {//待删除的公寓还有剩余房间，按业务逻辑，不能直接删除，需要提示用户。
            //直接为前端返回如下响应：先删除房间信息再删除公寓信息
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);//自定义异常，并被全局异常处理器捕获
        }
        //删除关联的表记录，因为都是多对多的关系，所以删除的都是中间表记录。除了GraphInfo。
        //1.删除GraphInfo
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);

        //2.删除ApartmentLabel
        LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(labelQueryWrapper);

        //3.删除ApartmentFacility
        LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(facilityQueryWrapper);

        //4.删除ApartmentFeeValue
        LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
        feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(feeQueryWrapper);
    }
}




