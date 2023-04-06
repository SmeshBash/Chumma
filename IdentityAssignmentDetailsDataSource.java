package com.cf.custom.reports;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.*;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import sailpoint.api.SailPointContext;
import sailpoint.object.*;
import sailpoint.object.Sort;
import sailpoint.reporting.datasource.JavaDataSource;
import sailpoint.task.Monitor;
import sailpoint.tools.GeneralException;

public class identityassignmentsdetailsdatasource implements JavaDataSource {

	QueryOptions baseQueryOptions;
	static int recordCount = 0;
	Integer startRow;
	Integer pageSize;
	Iterator finalobjects;
	Map customQueryOptions = new HashMap();
	Map object = new HashMap();
	SailPointContext context;
	List objectList = new ArrayList();
	public final int maxThreshold = 1000;
	String isClassName = "IdentityAssignmentDetailsDataSource";
	Log logger = LogFactory.getLog("com.cf.custom.reports.IdentityAssignmentDetailsDataSource");
	Connection connection = null;

	@Override
	public String getBaseHql() {
		return null;
	}

	@Override
	public QueryOptions getBaseQueryOptions() {
		return baseQueryOptions;
	}

	@Override
	public Object getFieldValue(String fieldName) throws GeneralException {

		if (fieldName.equals("identity")) {

			return (String) this.object.get("userName");
		} else if (fieldName.equals("nativeIdentity")) {

			return (String) this.object.get("nativeUserName");
		} else if (fieldName.equals("businessRole")) {

			return (String) this.object.get("busRoleName");
		} else if (fieldName.equals("itRole")) {

			return (String) this.object.get("childRoleName");
		} else if (fieldName.equals("application")) {

			return (String) this.object.get("applicationName");
		} else if (fieldName.equals("entitlement")) {

			return (String) this.object.get("endPointEntName");
		} else if (fieldName.equals("addedDate")) {

			return (String) this.object.get("lastAddedDate");
		} else if (fieldName.equals("lastAuditDate")) {

			return (String) this.object.get("lastCertifiedDate");
		} else {
			logger.error(" fieldName Not found :: " + fieldName);
			throw new GeneralException("Unknown column '" + fieldName + "'");
		}

	}

	@Override
	public int getSizeEstimate() throws GeneralException {
		return 0;
	}

	@Override
	public void close() {

	}

	@Override
	public void setMonitor(Monitor arg0) {

	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		String fieldName = jrField.getName();
		try {
			return getFieldValue(fieldName);

		} catch (GeneralException e) {
			throw new JRException(e);
		}
	}

	@Override
	public boolean next() throws JRException {

		boolean hasMore = false;

		if (this.finalobjects != null) {
			hasMore = this.finalobjects.hasNext();
			if (hasMore) {
				this.object = this.finalobjects.next();
			} else {
				this.object = null;
			}

		}
		return hasMore;
	}

	@Override
	public void initialize(SailPointContext context, LiveReport report, Attributes<String, Object> arguments,
			String groupBy, List<Sort> sort) throws GeneralException {
		String lsMethodName = "initialize";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");
		logger.info(isClassName + ":" + lsMethodName + ":" + "arguments : " + arguments);

		this.context = context;
		baseQueryOptions = new QueryOptions();
		try {
			if (arguments.containsKey("identity")) {
				String identity = (String) arguments.get("identity");
				customQueryOptions.put("Identity", identity);

			}
			if (arguments.containsKey("application")) {
				String application = (String) arguments.get("application");
				customQueryOptions.put("Application", application);

			}
			if (arguments.containsKey("entitlement")) {
				String entitlement = (String) arguments.get("entitlement");
				customQueryOptions.put("Entitlement", entitlement);

			}
			if (arguments.containsKey("itRole")) {
				String itRole = (String) arguments.get("itRole");
				customQueryOptions.put("ItRole", itRole);

			}
			if (arguments.containsKey("businessRole")) {
				String businessRole = (String) arguments.get("businessRole");
				customQueryOptions.put("BusinessRole", businessRole);

			}
			logger.info("customQueryOptions :: " + customQueryOptions.toString());
			prepare();
		} catch (Exception ex) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + ex.getMessage());
			ex.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");
		}

		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");

	}

	private void prepare() throws GeneralException {
		String lsMethodName = "prepare";
		long start = System.currentTimeMillis();
		long finish = 0l;

		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");
		String application = "NA";
		String businessRole = "NA";
		String entitlement = "NA";
		String itRole = "NA";
		String identity = "NA";
		try {
			logger.info("In Prepare try block");
			System.out.println("In Try block");
			logger.info("customQueryOptions :: " + customQueryOptions.toString());
			if (null != customQueryOptions.get("BusinessRole")) {
				businessRole = customQueryOptions.get("BusinessRole");

			}
			if (null != customQueryOptions.get("ItRole")) {
				itRole = customQueryOptions.get("ItRole");
			}
			if (null != customQueryOptions.get("Entitlement")) {
				entitlement = customQueryOptions.get("Entitlement");
			}
			if (null != customQueryOptions.get("Application")) {
				application = customQueryOptions.get("Application");
			}
			if (null != customQueryOptions.get("Identity")) {
				identity = customQueryOptions.get("Identity");
			}

			logger.info("Input Arguments");
			logger.info("identity :: " + identity);
			logger.info("businessRole :: " + businessRole);
			logger.info("itRole :: " + itRole);
			logger.info("entitlement :: " + entitlement);
			logger.info("application :: " + application);
			connection = context.getJdbcConnection();

			// Filter based on Identity.

			if ((identity != null && !identity.equals("NA"))) {

				logger.info("identity :: " + identity);
				String query = " and sie.identity_id='" + identity + "'";
				getBusRoleEntDB(query);
				getBusRoleEntDBMulti(query);
				getItRoleEntDB(query);
				getItRoleEntDBMulti(query);
				getAssignedItRoleEntDB(query);
				getAssignedItRoleEntDBMulti(query);
				getDirectlyAssignedEntDB(query);
				context.decache();
			}
			// Filter based on business role
			else if ((businessRole != null && !businessRole.equals("NA"))) {
				logger.info("In BusinessRole Filter :: ");
				logger.info("businessRole :: " + businessRole);
				String query = " and sbp.id='" + businessRole + "'";
				getBusRoleEntDB(query);
				getBusRoleEntDBMulti(query);
				context.decache();
			}

			// filter based on it role
			else if ((itRole != null && !itRole.equals("NA"))) {
				logger.info("In ITRole Filter :: ");
				logger.info("itRole :: " + itRole);
				String query = " and sbc.id='" + itRole + "'";
				getBusRoleEntDB(query);
				getBusRoleEntDBMulti(query);
				getItRoleEntDB(query);
				getItRoleEntDBMulti(query);
				getAssignedItRoleEntDB(query);
				getAssignedItRoleEntDBMulti(query);
				context.decache();

			}

			// filter based on application
			else if ((application != null && !application.equals("NA"))) {

				logger.info("In Application Filter :: ");
				logger.info("Application :: " + application);
				String query = " and sa.id='" + application + "'";
				getBusRoleEntDB(query);
				getBusRoleEntDBMulti(query);
				getItRoleEntDB(query);
				getItRoleEntDBMulti(query);
				getAssignedItRoleEntDB(query);
				getAssignedItRoleEntDBMulti(query);
				getDirectlyAssignedEntDB(query);
				context.decache();

			}

			// filter based on entitlement

			else if ((entitlement != null && !entitlement.equals("NA"))) {
				logger.info("Inside entitlements filter");
				logger.info("entitlements  ::" + entitlement);
				String query1 = " and spc.elt like '%" + entitlement + "%'";
				String query = " and sie.value='" + entitlement + "'";
				getBusRoleEntDB(entitlement,query1);
				getBusRoleEntDBMulti(entitlement, query1);
				getItRoleEntDB(entitlement, query1);
				getItRoleEntDBMulti(entitlement, query1);
				getAssignedItRoleEntDB(entitlement, query1);
				getAssignedItRoleEntDBMulti(entitlement, query1);
				getDirectlyAssignedEntDB(query);
				context.decache();

			}
			// no filter
			else {

				String query = " ";
				getBusRoleEntDB(query);
				getBusRoleEntDBMulti(query);
				getItRoleEntDB(query);
				getItRoleEntDBMulti(query);
				getAssignedItRoleEntDB(query);
				getAssignedItRoleEntDBMulti(query);
				getDirectlyAssignedEntDB(query);
				context.decache();

			}
			logger.info("objectList size :: " + objectList.size());
			finalobjects = objectList.iterator();
			finish = System.currentTimeMillis();
			connection.close();
			long timeElapsed = finish - start;
			logger.error("timeElapsed :: " + timeElapsed);

		} catch (Exception ex) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + ex.getMessage());
			ex.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");

		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");

	}

	@Override
	public void setLimit(int startRow, int pageSize) {
		this.startRow = startRow;
		this.pageSize = pageSize;

	}

	public ArrayList getEntitlements(String elt) {
		String lsMethodName = "getEntitlements";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");
		ArrayList returnList = new ArrayList();
		
		if(1 == 1)
		{
		logger.info("In getEntilement methos :: ");
		}
		
		
		try {
			String tempStr = elt.substring(elt.indexOf("{") + 1, elt.indexOf("}"));
			String ent;
			int len = 0;
			if (tempStr.contains("\","))
				for (String str_complex : tempStr.split("\",")) {

					ent = str_complex.trim();
					len = ent.length();
					if (ent.endsWith("\"")) {
						ent = ent.substring(1, len - 1);
						returnList.add(ent);
					} else {
						ent = ent.substring(1, len);
						returnList.add(ent);
					}
				}
			else {
				len = tempStr.length();
				if (tempStr.endsWith("\"")) {
					ent = tempStr.substring(1, len - 1);
					returnList.add(ent);
				} else {
					ent = tempStr.substring(1, len);
					returnList.add(ent);
				}
			}

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");
		return returnList;

	}

	void getDirectlyAssignedEntDB(String query) {
		String lsMethodName = "getDirectlyAssignedEntDB";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;

			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String certItemId = null;
			String applicationName = null;
			String ent = null;
			String nativeUserName = null;
			String userName = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = "select sa.name as appname,sie.value,si.name as username,sie.native_identity,sie.created,sie.certification_item FROM spt_identity si,spt_identity_entitlement sie, spt_application sa "
					+ " where sie.application=sa.id " + " and si.id=sie.identity_id "
					+ " and (sie.attributes is null or sie.attributes=' ') " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				try {
					createtimeStamp = rs.getLong("created");
					if (createtimeStamp > 0l) {
						createDateObj = new Timestamp(createtimeStamp);
						lsCreateDate = formatter.format(createDateObj);
					} else {
						lsCreateDate = "NA";
					}

					certItemId = rs.getString("certification_item");

					if (certItemId != null) {
						sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
						logger.info("SQL 1  : " + sql1);
						stmt1 = connection.createStatement();
						rs1 = stmt1.executeQuery(sql1);
						if (rs1.next()) {
							certtimeStamp = rs1.getLong("finished_date");
							if (certtimeStamp > 0l) {
								lastCertifiedDateObj = new Timestamp(certtimeStamp);
								lastCertifiedDate = formatter.format(lastCertifiedDateObj);
							}
						}
						

					} else {
						lastCertifiedDate = "NA";
					}
					applicationName = rs.getObject("appname");
					ent = rs.getString("value");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");

					itemMap = new HashMap<String, Object>();
					itemMap.put("busRoleName", "OPEN");
					itemMap.put("childRoleName", "OPEN");
					itemMap.put("applicationName", applicationName);
					itemMap.put("endPointEntName", ent);
					itemMap.put("userName", userName);
					itemMap.put("nativeUserName", nativeUserName);
					itemMap.put("lastAddedDate", lsCreateDate);
					itemMap.put("lastCertifiedDate", lastCertifiedDate);
					logger.info("itemMap :: " + itemMap.toString());
					objectList.add(itemMap);
					 recordCount++;
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}

			}
			

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getBusRoleEntDB(String query) {
		String lsMethodName = "getBusRoleEntDB(query)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String certItemId = null;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as busrolename,sbc.name itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbp.id as busroleid,sbr.child as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie , spt_bundle sbp,spt_bundle sbc,spt_bundle_requirements sbr, spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbp.name=sie.value" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbp.id=sbr.bundle" + " and sbr.child=sp.bundle_id" + " and sbc.id= sbr.child"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id not in ( select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application"
					+ " and sie.name ='assignedRoles' and sie.assignment_id is not null " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {

				try {
					

					String busRoleName = rs.getString("busrolename");
					String childRoleName = rs.getString("itrolename");
					String userName = rs.getString("username");
					String nativeUserName = rs.getString("native_identity");
					String applicationName = rs.getObject("appname");
					String elt = rs.getString("elt");
					createtimeStamp = rs.getLong("created");
					if (createtimeStamp > 0l) {
						createDateObj = new Timestamp(createtimeStamp);
						lsCreateDate = formatter.format(createDateObj);
					} else {
						lsCreateDate = "NA";
					}

					certItemId = rs.getString("certification_item");

					
						sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
						stmt1 = connection.createStatement();
						rs1 = stmt1.executeQuery(sql1);
						if (rs1.next()) {
							certtimeStamp = rs1.getLong("finished_date");
							if (certtimeStamp > 0l) {
								lastCertifiedDateObj = new Timestamp(certtimeStamp);
								lastCertifiedDate = formatter.format(lastCertifiedDateObj);
							}
						}
					

					   
					   	try {
			String tempStr = elt.substring(elt.indexOf("{") + 1, elt.indexOf("}"));
			String ent;
			int len = 0;
			if (tempStr.contains("\","))
				for (String str_complex : tempStr.split("\",")) {

					ent = str_complex.trim();
					len = ent.length();
					if (ent.endsWith("\"")) {
						ent = ent.substring(1, len - 1);
						entList.add(ent);
					} else {
						ent = ent.substring(1, len);
						entList.add(ent);
					}
				}
			else {
				len = tempStr.length();
				if (tempStr.endsWith("\"")) {
					ent = tempStr.substring(1, len - 1);
					entList.add(ent);
				} else {
					ent = tempStr.substring(1, len);
					entList.add(ent);
				}
			}

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");
		}
					   
					   
					   
					   
					   

			
					logger.info(" entList  ::::" + entList.toString());
					for (String ent : entList) {
						itemMap = new HashMap<String, Object>();
						itemMap.put("busRoleName", busRoleName);
						itemMap.put("childRoleName", childRoleName);
						itemMap.put("applicationName", applicationName);
						itemMap.put("endPointEntName", ent);
						itemMap.put("userName", userName);
						itemMap.put("nativeUserName", nativeUserName);
						itemMap.put("lastAddedDate", lsCreateDate);
						itemMap.put("lastCertifiedDate", lastCertifiedDate);
						logger.info("itemMap :: " + itemMap.toString());
						objectList.add(itemMap);
					    recordCount++;
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}

		

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getBusRoleEntDBMulti(String query) {
		String lsMethodName = "getBusRoleEntDBMulti(query)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String lsCreateDate = null;
			String certItemId = null;
			ArrayList<String> entList = null;
			Identity identityObj = null;
			String identityId = null;
			RoleAssignment roleAssignment = null;
			List<RoleTarget> roleTargetList = null;
			String assignmentId = null;
			boolean checkRole = false;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as busrolename,sbc.name itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbp.id as busroleid,sbr.child as itroleid,sp.application,sie.assignment_id FROM spt_identity si,spt_identity_entitlement sie , spt_bundle sbp,spt_bundle sbc,spt_bundle_requirements sbr, spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbp.name=sie.value" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbp.id=sbr.bundle" + " and sbr.child=sp.bundle_id" + " and sbc.id= sbr.child"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id in (select identity_id  FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application"
					+ " and sie.name ='assignedRoles' and sie.assignment_id is not null " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				try {
					busRoleName = rs.getString("busrolename");
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					identityId = rs.getString("identity_id");
					assignmentId = rs.getString("assignment_id");
					identityObj = context.getObjectById(Identity.class, identityId);
					roleAssignment = identityObj.getRoleAssignmentById(assignmentId);
					checkRole = false;
					if (roleAssignment != null) {
						roleTargetList = roleAssignment.getTargets();
						if (roleTargetList != null && roleTargetList.size() > 0) {
							for (RoleTarget eachTarget : roleTargetList) {
								if (eachTarget.getApplicationName().equals(applicationName)
										&& eachTarget.getNativeIdentity().equals(nativeUserName)) {
									checkRole = true;
								}
							}
						}
					}
					if (checkRole) {
					   System.out.println("In Check Role block");
						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}

						certItemId = rs.getString("certification_item");

						
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
						
						
						
						 	try {
			String tempStr = elt.substring(elt.indexOf("{") + 1, elt.indexOf("}"));
			String ent;
			int len = 0;
			if (tempStr.contains("\","))
				for (String str_complex : tempStr.split("\",")) {

					ent = str_complex.trim();
					len = ent.length();
					if (ent.endsWith("\"")) {
						ent = ent.substring(1, len - 1);
						entList.add(ent);
					} else {
						ent = ent.substring(1, len);
						entList.add(ent);
					}
				}
			else {
				len = tempStr.length();
				if (tempStr.endsWith("\"")) {
					ent = tempStr.substring(1, len - 1);
					entList.add(ent);
				} else {
					ent = tempStr.substring(1, len);
					entList.add(ent);
				}
			}

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited ");
		}

					
						logger.info(" entList  ::::" + entList.toString());
						for (String ent : entList) {
							itemMap = new HashMap<String, Object>();
							itemMap.put("busRoleName", busRoleName);
							itemMap.put("childRoleName", childRoleName);
							itemMap.put("applicationName", applicationName);
							itemMap.put("endPointEntName", ent);
							itemMap.put("userName", userName);
							itemMap.put("nativeUserName", nativeUserName);
							itemMap.put("lastAddedDate", lsCreateDate);
							itemMap.put("lastCertifiedDate", lastCertifiedDate);
							logger.info("itemMap :: " + itemMap.toString());
							objectList.add(itemMap);
						    recordCount++;
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}

		
		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getBusRoleEntDB(String query, String entValue) {
		String lsMethodName = "getBusRoleEntDB(query,entValue)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String lsCreateDate = null;
			String certItemId = null;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as busrolename,sbc.name itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbp.id as busroleid,sbr.child as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie , spt_bundle sbp,spt_bundle sbc,spt_bundle_requirements sbr, spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbp.name=sie.value" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbp.id=sbr.bundle" + " and sbr.child=sp.bundle_id" + " and sbc.id= sbr.child"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sp.application=sl.application"
					+ " and sie.identity_id not in ( select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sie.name ='assignedRoles' and sie.assignment_id is not null " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				try {
					busRoleName = rs.getString("busrolename");
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					elt = rs.getString("elt");
					createtimeStamp = rs.getLong("created");
					if (createtimeStamp > 0l) {
						createDateObj = new Timestamp(createtimeStamp);
						lsCreateDate = formatter.format(createDateObj);
					} else {
						lsCreateDate = "NA";
					}

					certItemId = rs.getString("certification_item");

						sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
						stmt1 = connection.createStatement();
						rs1 = stmt1.executeQuery(sql1);
						if (rs1.next()) {
							certtimeStamp = rs1.getLong("finished_date");
							if (certtimeStamp > 0l) {
								lastCertifiedDateObj = new Timestamp(certtimeStamp);
								lastCertifiedDate = formatter.format(lastCertifiedDateObj);
							}
						}
						

					

					entList = getEntitlements(elt);
					logger.info(" entList  ::::" + entList.toString());
					if (entList != null && entList.contains(entValue)) {
						for (String ent : entList) {
							if (ent.equals(entValue)) {
								itemMap = new HashMap<String, Object>();
								itemMap.put("busRoleName", busRoleName);
								itemMap.put("childRoleName", childRoleName);
								itemMap.put("applicationName", applicationName);
								itemMap.put("endPointEntName", ent);
								itemMap.put("userName", userName);
								itemMap.put("nativeUserName", nativeUserName);
								itemMap.put("lastAddedDate", lsCreateDate);
								itemMap.put("lastCertifiedDate", lastCertifiedDate);
								logger.info("itemMap :: " + itemMap.toString());
								objectList.add(itemMap);
								recordCount++;
							}
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}

			}
			

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getBusRoleEntDBMulti(String query, String entValue) {
		String lsMethodName = "getBusRoleEntDBMulti(query,entValue)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String lsCreateDate = null;
			String certItemId = null;
			Identity identityObj = null;
			String identityId = null;
			RoleAssignment roleAssignment = null;
			List<RoleTarget> roleTargetList = null;
			String assignmentId = null;
			boolean checkRole = false;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as busrolename,sbc.name itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbp.id as busroleid,sbr.child as itroleid,sp.application,sie.assignment_id FROM spt_identity si,spt_identity_entitlement sie , spt_bundle sbp,spt_bundle sbc,spt_bundle_requirements sbr, spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbp.name=sie.value" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbp.id=sbr.bundle" + " and sbr.child=sp.bundle_id" + " and sbc.id= sbr.child"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sp.application=sl.application"
					+ " and sie.identity_id in ( select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sie.name ='assignedRoles' and sie.assignment_id is not null " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				try {
					busRoleName = rs.getString("busrolename");
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					identityId = rs.getString("identity_id");
					assignmentId = rs.getString("assignment_id");
					identityObj = context.getObjectById(Identity.class, identityId);
					roleAssignment = identityObj.getRoleAssignmentById(assignmentId);
					checkRole = false;
					if (roleAssignment != null) {
						roleTargetList = roleAssignment.getTargets();
						if (roleTargetList != null && roleTargetList.size() > 0) {
							for (RoleTarget eachTarget : roleTargetList) {
								if (eachTarget.getApplicationName().equals(applicationName)
										&& eachTarget.getNativeIdentity().equals(nativeUserName)) {
									checkRole = true;
								}
							}
						}
					}
					if (checkRole) {
						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}

						certItemId = rs.getString("certification_item");

						
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
							

						

						entList = getEntitlements(elt);
						logger.info(" entList  ::::" + entList.toString());
						if (entList != null && entList.contains(entValue)) {
							for (String ent : entList) {
								if (ent.equals(entValue)) {
									itemMap = new HashMap<String, Object>();
									itemMap.put("busRoleName", busRoleName);
									itemMap.put("childRoleName", childRoleName);
									itemMap.put("applicationName", applicationName);
									itemMap.put("endPointEntName", ent);
									itemMap.put("userName", userName);
									itemMap.put("nativeUserName", nativeUserName);
									itemMap.put("lastAddedDate", lsCreateDate);
									itemMap.put("lastCertifiedDate", lastCertifiedDate);
									logger.info("itemMap :: " + itemMap.toString());
									objectList.add(itemMap);
									recordCount++;
								}
							}
						}

					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getItRoleEntDB(String query) {
		String lsMethodName = "getItRoleEntDB(query)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;

			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = "SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='detectedRoles'" + " and sie.assignment_id is null"
					+ " and sie.identity_id=si.id" + " and sa.id=sp.application" + " and sbc.id=sp.bundle_id"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id not in (select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					elt = rs.getString("elt");
					createtimeStamp = rs.getLong("created");
					if (createtimeStamp > 0l) {
						createDateObj = new Timestamp(createtimeStamp);
						lsCreateDate = formatter.format(createDateObj);
					} else {
						lsCreateDate = "NA";
					}

					certItemId = rs.getString("certification_item");

					
						sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
						stmt1 = connection.createStatement();
						rs1 = stmt1.executeQuery(sql1);
						if (rs1.next()) {
							certtimeStamp = rs1.getLong("finished_date");
							if (certtimeStamp > 0l) {
								lastCertifiedDateObj = new Timestamp(certtimeStamp);
								lastCertifiedDate = formatter.format(lastCertifiedDateObj);
							}
						}
						

					

					entList = getEntitlements(elt);
					logger.info(" entList  ::::" + entList.toString());
					for (String ent : entList) {

						itemMap = new HashMap<String, Object>();
						itemMap.put("busRoleName", busRoleName);
						itemMap.put("childRoleName", childRoleName);
						itemMap.put("applicationName", applicationName);
						itemMap.put("endPointEntName", ent);
						itemMap.put("userName", userName);
						itemMap.put("nativeUserName", nativeUserName);
						itemMap.put("lastAddedDate", lsCreateDate);
						itemMap.put("lastCertifiedDate", lastCertifiedDate);
						logger.info("itemMap :: " + itemMap.toString());
						objectList.add(itemMap);
					    recordCount++;
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getItRoleEntDBMulti(String query) {
		String lsMethodName = "getItRoleEntDBMulti(query)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String NATIVEUSERNAME = null;
			String USERNAME = null;
			String certItemId = null;
			Identity identityObj = null;
			String identityId = null;
			List<RoleTarget> roleTargetList = null;
			List<RoleDetection> roleDetectionList = null;
			Application appObj = null;
			boolean checkRole = false;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = "SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='detectedRoles'" + " and sie.assignment_id is null"
					+ " and sie.identity_id=si.id" + " and sa.id=sp.application" + " and sbc.id=sp.bundle_id"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id in (select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					USERNAME = rs.getString("username");
					NATIVEUSERNAME = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					identityId = rs.getString("identity_id");
					appObj = context.getObjectByName(Application.class, applicationName);
					checkRole = false;
					identityObj = context.getObjectById(Identity.class, identityId);
					roleDetectionList = identityObj.getRoleDetections(appObj);
					if (roleDetectionList != null && roleDetectionList.size() > 0) {
						for (RoleDetection eachDetection : roleDetectionList) {
							if (eachDetection.getRoleName().equals(childRoleName)) {
								logger.info("roleDetection  :: " + eachDetection.toString());
								roleTargetList = eachDetection.getTargets();
								if (roleTargetList != null && roleTargetList.size() > 0) {
									for (RoleTarget eachTarget : roleTargetList) {
										if (eachTarget.getApplicationName().equals(applicationName)
												&& eachTarget.getNativeIdentity().equals(NATIVEUSERNAME)) {
											checkRole = true;
										}
									}
								}
							}
						}
					}
					logger.info("checkRole  :: " + checkRole);
					if (checkRole) {
						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}

						certItemId = rs.getString("certification_item");

						
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
							


						entList = getEntitlements(elt);
						logger.info(" entList  ::::" + entList.toString());
						for (String ent : entList) {

							itemMap = new HashMap<String, Object>();
							itemMap.put("busRoleName", busRoleName);
							itemMap.put("childRoleName", childRoleName);
							itemMap.put("applicationName", applicationName);
							itemMap.put("endPointEntName", ent);
							itemMap.put("userName", USERNAME);
							itemMap.put("nativeUserName", NATIVEUSERNAME);
							itemMap.put("lastAddedDate", lsCreateDate);
							itemMap.put("lastCertifiedDate", lastCertifiedDate);
							logger.info("itemMap :: " + itemMap.toString());
							objectList.add(itemMap);
							recordCount++;
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
			

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getItRoleEntDB(String query, String entValue) {
		String lsMethodName = "getItRoleEntDB(query,entValue)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = "SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='detectedRoles'" + " and sie.assignment_id is null"
					+ " and sie.identity_id=si.id" + " and sa.id=sp.application" + " and sbc.id=sp.bundle_id"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id "
					+ " and sie.identity_id not in ( select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					elt = rs.getString("elt");
					createtimeStamp = rs.getLong("created");
					if (createtimeStamp > 0l) {
						createDateObj = new Timestamp(createtimeStamp);
						lsCreateDate = formatter.format(createDateObj);
					} else {
						lsCreateDate = "NA";
					}

					certItemId = rs.getString("certification_item");

					
						sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
						stmt1 = connection.createStatement();
						rs1 = stmt1.executeQuery(sql1);
						if (rs1.next()) {
							certtimeStamp = rs1.getLong("finished_date");
							if (certtimeStamp > 0l) {
								lastCertifiedDateObj = new Timestamp(certtimeStamp);
								lastCertifiedDate = formatter.format(lastCertifiedDateObj);
							}
						}
						

				

					entList = getEntitlements(elt);
					logger.info(" entList  ::::" + entList.toString());
					if (entList != null && entList.contains(entValue)) {
						for (String ent : entList) {
							if (ent.equals(entValue)) {
								itemMap = new HashMap<String, Object>();
								itemMap.put("busRoleName", busRoleName);
								itemMap.put("childRoleName", childRoleName);
								itemMap.put("applicationName", applicationName);
								itemMap.put("endPointEntName", ent);
								itemMap.put("userName", userName);
								itemMap.put("nativeUserName", nativeUserName);
								itemMap.put("lastAddedDate", lsCreateDate);
								itemMap.put("lastCertifiedDate", lastCertifiedDate);
								logger.info("itemMap :: " + itemMap.toString());
								objectList.add(itemMap);
							    recordCount++;
							}
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
			

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getItRoleEntDBMulti(String query, String entValue) {
		String lsMethodName = "getItRoleEntDBMulti(query,entValue)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;
			Identity identityObj = null;
			String identityId = null;
			List<RoleTarget> roleTargetList = null;
			List<RoleDetection> roleDetectionList = null;
			Application appObj = null;
			boolean checkRole = false;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = "SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='detectedRoles'" + " and sie.assignment_id is null"
					+ " and sie.identity_id=si.id" + " and sa.id=sp.application" + " and sbc.id=sp.bundle_id"
					+ " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id "
					+ " and sie.identity_id in ( select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getString("appname");
					identityId = rs.getString("identity_id");
					appObj = context.getObjectByName(Application.class, applicationName);
					checkRole = false;
					identityObj = context.getObjectById(Identity.class, identityId);
					roleDetectionList = identityObj.getRoleDetections(appObj);
					if (roleDetectionList != null && roleDetectionList.size() > 0) {
						for (RoleDetection eachDetection : roleDetectionList) {
							if (eachDetection.getRoleName().equals(childRoleName)) {
								logger.info("roleDetection  :: " + eachDetection.toString());
								roleTargetList = eachDetection.getTargets();
								if (roleTargetList != null && roleTargetList.size() > 0) {
									for (RoleTarget eachTarget : roleTargetList) {
										if (eachTarget.getApplicationName().equals(applicationName)
												&& eachTarget.getNativeIdentity().equals(nativeUserName)) {
											checkRole = true;
										}
									}
								}
							}
						}
					}
					logger.info("checkRole  :: " + checkRole);
					if (checkRole) {

						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}

						certItemId = rs.getString("certification_item");

						
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
							
						entList = getEntitlements(elt);
						logger.info(" entList  ::::" + entList.toString());
						if (entList != null && entList.contains(entValue)) {
							for (String ent : entList) {
								if (ent.equals(entValue)) {
									Map itemMap = new HashMap<String, Object>();
									itemMap.put("busRoleName", busRoleName);
									itemMap.put("childRoleName", childRoleName);
									itemMap.put("applicationName", applicationName);
									itemMap.put("endPointEntName", ent);
									itemMap.put("userName", userName);
									itemMap.put("nativeUserName", nativeUserName);
									itemMap.put("lastAddedDate", lsCreateDate);
									itemMap.put("lastCertifiedDate", lastCertifiedDate);
									logger.info("itemMap :: " + itemMap.toString());
									objectList.add(itemMap);
									String outputString=busRoleName+ ":"+childRoleName+":"+applicationName+":"+ent+":"+userName+":"+nativeUserName+":"+lsCreateDate+":"+lastCertifiedDate;
									logger.info("outputString :: " + outputString);
									recordCount++;
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
			

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getAssignedItRoleEntDB(String query) {
		String lsMethodName = "getAssignedItRoleEntDB(query)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;
			Identity identityObj = null;
			String identityId = null;
			List<RoleTarget> roleTargetList = null;
			List<RoleDetection> roleDetectionList = null;
			Application appObj = null;
			boolean checkRole = false;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='assignedRoles'"
					+ " and sie.assignment_id is not null" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbc.id=sp.bundle_id" + " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id not in (select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application" + " and sbc.type in ('it','birthRightIT') " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					identityId = rs.getString("identity_id");
					appObj = context.getObjectByName(Application.class, applicationName);
					checkRole = false;
					identityObj = context.getObjectById(Identity.class, identityId);
					roleDetectionList = identityObj.getRoleDetections(appObj);
					if (roleDetectionList != null && roleDetectionList.size() > 0) {
						for (RoleDetection eachDetection : roleDetectionList) {
							if (eachDetection.getRoleName().equals(childRoleName)) {
								logger.info("roleDetection  :: " + eachDetection.toString());
								roleTargetList = eachDetection.getTargets();
								if (roleTargetList != null && roleTargetList.size() > 0) {
									for (RoleTarget eachTarget : roleTargetList) {
										if (eachTarget.getApplicationName().equals(applicationName)
												&& eachTarget.getNativeIdentity().equals(nativeUserName)) {
											checkRole = true;
										}
									}
								}
							}
						}
					}
					logger.info("checkRole  :: " + checkRole);
					if (checkRole) {

						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}
						certItemId = rs.getString("certification_item");
					
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
						
						entList = getEntitlements(elt);
						logger.info(" entList  ::::" + entList.toString());
						for (String ent : entList) {

							itemMap = new HashMap<String, Object>();
							itemMap.put("busRoleName", busRoleName);
							itemMap.put("childRoleName", childRoleName);
							itemMap.put("applicationName", applicationName);
							itemMap.put("endPointEntName", ent);
							itemMap.put("userName", userName);
							itemMap.put("nativeUserName", nativeUserName);
							itemMap.put("lastAddedDate", lsCreateDate);
							itemMap.put("lastCertifiedDate", lastCertifiedDate);
							logger.info("itemMap :: " + itemMap.toString());
							objectList.add(itemMap);
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getAssignedItRoleEntDBMulti(String query) {
		String lsMethodName = "getAssignedItRoleEntDBMulti(query)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;
			Identity identityObj = null;
			String identityId = null;
			List<RoleTarget> roleTargetList = null;
			List<RoleDetection> roleDetectionList = null;
			Application appObj = null;
			boolean checkRole = false;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='assignedRoles'"
					+ " and sie.assignment_id is not null" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbc.id=sp.bundle_id" + " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id in (select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application" + " and sbc.type in ('it','birthRightIT') " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					logger.info(" username  ::::" + userName);
					nativeUserName = rs.getString("native_identity");
					logger.info(" nativeUserName  ::::" + nativeUserName);
					applicationName = rs.getObject("appname");
					identityId = rs.getString("identity_id");
					appObj = context.getObjectByName(Application.class, applicationName);
					checkRole = false;
					identityObj = context.getObjectById(Identity.class, identityId);
					roleDetectionList = identityObj.getRoleDetections(appObj);
					if (roleDetectionList != null && roleDetectionList.size() > 0) {
						for (RoleDetection eachDetection : roleDetectionList) {
							if (eachDetection.getRoleName().equals(childRoleName)) {
								logger.info("roleDetection  :: " + eachDetection.toString());
								roleTargetList = eachDetection.getTargets();
								if (roleTargetList != null && roleTargetList.size() > 0) {
									for (RoleTarget eachTarget : roleTargetList) {
										if (eachTarget.getApplicationName().equals(applicationName)
												&& eachTarget.getNativeIdentity().equals(nativeUserName)) {
											checkRole = true;
										}
									}
								}
							}
						}
					}
					logger.info("checkRole  :: " + checkRole);
					if (checkRole) {
						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}

						certItemId = rs.getString("certification_item");

						
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
						

						

						entList = getEntitlements(elt);
						logger.info(" entList  ::::" + entList.toString());
						for (String ent : entList) {

							itemMap = new HashMap<String, Object>();
							itemMap.put("busRoleName", busRoleName);
							itemMap.put("childRoleName", childRoleName);
							itemMap.put("applicationName", applicationName);
							itemMap.put("endPointEntName", ent);
							itemMap.put("userName", userName);
							itemMap.put("nativeUserName", nativeUserName);
							itemMap.put("lastAddedDate", lsCreateDate);
							itemMap.put("lastCertifiedDate", lastCertifiedDate);
							logger.info("itemMap :: " + itemMap.toString());
							objectList.add(itemMap);
							recordCount++;
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
			

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getAssignedItRoleEntDB(String query, String entValue) {
		String lsMethodName = "getAssignedItRoleEntDB(query,entValue)";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = "SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='assignedRoles'"
					+ " and sie.assignment_id is not null" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbc.id=sp.bundle_id" + " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id not in (select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application" + " and sbc.type in ('it','birthRightIT') " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					logger.info(" username  ::::" + userName);
					nativeUserName = rs.getString("native_identity");
					applicationName = rs.getObject("appname");
					logger.info(" nativeUserName  ::::" + nativeUserName);
					elt = rs.getString("elt");
					createtimeStamp = rs.getLong("created");
					if (createtimeStamp > 0l) {
						createDateObj = new Timestamp(createtimeStamp);
						lsCreateDate = formatter.format(createDateObj);
					} else {
						lsCreateDate = "NA";
					}

					certItemId = rs.getString("certification_item");

					
						sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
						stmt1 = connection.createStatement();
						rs1 = stmt1.executeQuery(sql1);
						if (rs1.next()) {
							certtimeStamp = rs1.getLong("finished_date");
							if (certtimeStamp > 0l) {
								lastCertifiedDateObj = new Timestamp(certtimeStamp);
								lastCertifiedDate = formatter.format(lastCertifiedDateObj);
							}
						}
						

				

					entList = getEntitlements(elt);
					logger.info(" entList  ::::" + entList.toString());
					if (entList != null && entList.contains(entValue)) {
						for (String ent : entList) {
							if (ent.equals(entValue)) {
								itemMap = new HashMap<String, Object>();
								itemMap.put("busRoleName", busRoleName);
								itemMap.put("childRoleName", childRoleName);
								itemMap.put("applicationName", applicationName);
								itemMap.put("endPointEntName", ent);
								itemMap.put("userName", userName);
								itemMap.put("nativeUserName", nativeUserName);
								itemMap.put("lastAddedDate", lsCreateDate);
								itemMap.put("lastCertifiedDate", lastCertifiedDate);
								logger.info("itemMap :: " + itemMap.toString());
								objectList.add(itemMap);
								recordCount++;
							}
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

	void getAssignedItRoleEntDBMulti(String query, String entValue) {
		String lsMethodName = "getAssignedItRoleEntDB";
		logger.info(isClassName + ":" + lsMethodName + ":" + "Entered ");

		try {
			logger.info("query  :: " + query);
			Map<String, Object> itemMap = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			long createtimeStamp = 0l;
			long certtimeStamp = 0l;
			Date createDateObj = null;
			Date lastCertifiedDateObj = null;
			String lastCertifiedDate = null;
			String lsCreateDate = null;
			String busRoleName = null;
			String childRoleName = null;
			String applicationName = null;
			String elt = null;
			String nativeUserName = null;
			String userName = null;
			String certItemId = null;
			Identity identityObj = null;
			String identityId = null;
			List<RoleTarget> roleTargetList = null;
			List<RoleDetection> roleDetectionList = null;
			Application appObj = null;
			boolean checkRole = false;
			ArrayList<String> entList = null;
			Statement stmt = connection.createStatement();
			Statement stmt1 = null;
			ResultSet rs1 = null;
			String sql1 = null;
			String sql = " SELECT sie.value as itrolename,sie.identity_id,si.name as username,sa.name as appname,spc.elt,sl.native_identity,sie.created,sie.certification_item,sbc.id as itroleid,sp.application FROM spt_identity si,spt_identity_entitlement sie ,spt_bundle sbc,spt_profile sp,spt_profile_constraints spc,spt_link sl,spt_application sa"
					+ " where sbc.name=sie.value" + " and sie.name ='assignedRoles'"
					+ " and sie.assignment_id is not null" + " and sie.identity_id=si.id" + " and sa.id=sp.application"
					+ " and sbc.id=sp.bundle_id" + " and sp.id =spc.profile" + " and sie.identity_id=sl.identity_id"
					+ " and sie.identity_id in (select identity_id FROM spt_link group by application, identity_id having count(identity_id) > 1 )"
					+ " and sp.application=sl.application" + " and sbc.type in ('it','birthRightIT') " + query;

			logger.info("SQL  : " + sql);

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				try {
					busRoleName = "OPEN";
					childRoleName = rs.getString("itrolename");
					userName = rs.getString("username");
					logger.info(" username  ::::" + userName);
					nativeUserName = rs.getString("native_identity");
					logger.info(" nativeUserName  ::::" + nativeUserName);
					applicationName = rs.getObject("appname");
					identityId = rs.getString("identity_id");
					appObj = context.getObjectByName(Application.class, applicationName);
					checkRole = false;
					identityObj = context.getObjectById(Identity.class, identityId);
					roleDetectionList = identityObj.getRoleDetections(appObj);
					if (roleDetectionList != null && roleDetectionList.size() > 0) {
						for (RoleDetection eachDetection : roleDetectionList) {
							if (eachDetection.getRoleName().equals(childRoleName)) {
								logger.info("roleDetection  :: " + eachDetection.toString());
								roleTargetList = eachDetection.getTargets();
								if (roleTargetList != null && roleTargetList.size() > 0) {
									for (RoleTarget eachTarget : roleTargetList) {
										if (eachTarget.getApplicationName().equals(applicationName)
												&& eachTarget.getNativeIdentity().equals(nativeUserName)) {
											checkRole = true;
										}
									}
								}
							}
						}
					}
					logger.info("checkRole  :: " + checkRole);
					if (checkRole) {

						elt = rs.getString("elt");
						createtimeStamp = rs.getLong("created");
						if (createtimeStamp > 0l) {
							createDateObj = new Timestamp(createtimeStamp);
							lsCreateDate = formatter.format(createDateObj);
						} else {
							lsCreateDate = "NA";
						}

						certItemId = rs.getString("certification_item");

						
							sql1 = "select finished_date from spt_certification_item where id='" + certItemId + "'";
							stmt1 = connection.createStatement();
							rs1 = stmt1.executeQuery(sql1);
							if (rs1.next()) {
								certtimeStamp = rs1.getLong("finished_date");
								if (certtimeStamp > 0l) {
									lastCertifiedDateObj = new Timestamp(certtimeStamp);
									lastCertifiedDate = formatter.format(lastCertifiedDateObj);
								}
							}
							

						

						entList = getEntitlements(elt);
						logger.info(" entList  ::::" + entList.toString());
						if (entList != null && entList.contains(entValue)) {
							for (String ent : entList) {
								if (ent.equals(entValue)) {
									itemMap = new HashMap<String, Object>();
									itemMap.put("busRoleName", busRoleName);
									itemMap.put("childRoleName", childRoleName);
									itemMap.put("applicationName", applicationName);
									itemMap.put("endPointEntName", ent);
									itemMap.put("userName", userName);
									itemMap.put("nativeUserName", nativeUserName);
									itemMap.put("lastAddedDate", lsCreateDate);
									itemMap.put("lastCertifiedDate", lastCertifiedDate);
									logger.info("itemMap :: " + itemMap.toString());
									objectList.add(itemMap);
									recordCount++;
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		

		} catch (Exception e) {
			logger.error(isClassName + ":" + lsMethodName + ":" + "Exception : " + e.getMessage());
			e.printStackTrace();
			logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");
		}
		logger.info(isClassName + ":" + lsMethodName + ":" + "Exited");

	}

}
