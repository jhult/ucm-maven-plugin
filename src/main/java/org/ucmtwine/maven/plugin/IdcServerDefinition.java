package org.ucmtwine.maven.plugin;

import org.apache.maven.plugins.annotations.Parameter;

/**
 *  Object that defines the configured WCC Server
 */
public class IdcServerDefinition 
{

  /** UCM server id */
  @Parameter(required = true)
  private String id;

  /** UCM url and (idc) port e.g. idc://localhost:4444 */
  @Parameter(defaultValue = StringConstants.IDC_LOCALHOST_4444)
  private String url;

  /** UCM username */
  @Parameter(defaultValue = StringConstants.SYSADMIN)
  private String username;

  /** UCM user's password */
  @Parameter(defaultValue = StringConstants.IDC)
  private String password;

  /** Admin Server info */
  @Parameter private AdminServerDefinition adminServer;
  
  
  public String getId() { return id; }

  public void setId(final String id) { this.id = id; }

  public String getUrl() { return url; }

  public void setUrl(final String url) { this.url = url; }

  public String getUsername() { return username; }

  public void setUsername(final String username) { this.username = username; }

  public String getPassword() { return password; }

  public void setPassword(final String password) { this.password = password; }

  public AdminServerDefinition getAdminServer() 
  { 
     adminServer.setParentIdcServerDefinition(this);
     return adminServer; 
  }

  public void setAdminServer(final AdminServerDefinition adminServer)
  { this.adminServer = adminServer; }
}
