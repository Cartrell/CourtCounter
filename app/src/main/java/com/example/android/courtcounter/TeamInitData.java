package com.example.android.courtcounter;

class TeamInitData {
  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // member variables
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////
  private int m_nameResource;
  private int m_imageResource;

  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // ctor
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////
  TeamInitData(int nameResource, int imageResource) {
    m_nameResource = nameResource;
    m_imageResource = imageResource;
  }

  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // package private
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////

  //===========================================================================
  // imageResource
  //===========================================================================
  int imageResource() {
    return(m_imageResource);
  }

  //===========================================================================
  // nameResource
  //===========================================================================
  int nameResource() {
    return(m_nameResource);
  }
}
