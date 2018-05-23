package com.exp.demo.dao;

import com.exp.demo.pojo.*;

public interface SdSessionMapper
{
    int deleteByPrimaryKey(final String p0);
    
    int insert(final SdSession p0);
    
    int insertSelective(final SdSession p0);
    
    SdSession selectByPrimaryKey(final String p0);
    
    int updateByPrimaryKeySelective(final SdSession p0);
    
    int updateByPrimaryKey(final SdSession p0);
}
