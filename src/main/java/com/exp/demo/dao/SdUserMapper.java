package com.exp.demo.dao;

import com.exp.demo.pojo.*;

public interface SdUserMapper
{
    int deleteByPrimaryKey(final String p0);
    
    int insert(final SdUser p0);
    
    int insertSelective(final SdUser p0);
    
    SdUser selectByPrimaryKey(final String p0);
    
    int updateByPrimaryKeySelective(final SdUser p0);
    
    int updateByPrimaryKey(final SdUser p0);
}
