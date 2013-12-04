/*
 * 
 *	Copyright © 2013 Changsha Shishuo Network Technology Co., Ltd. All rights reserved.
 *	长沙市师说网络科技有限公司 版权所有
 *	http://www.shishuo.com
 *
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *	 
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */
package com.shishuo.cms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shishuo.cms.constant.CommentConstant;
import com.shishuo.cms.constant.FileConstant;
import com.shishuo.cms.constant.FileConstant.Picture;
import com.shishuo.cms.constant.SystemConstant;
import com.shishuo.cms.dao.FileDao;
import com.shishuo.cms.entity.Admin;
import com.shishuo.cms.entity.File;
import com.shishuo.cms.entity.Folder;
import com.shishuo.cms.entity.vo.FileVo;
import com.shishuo.cms.entity.vo.PageVo;
import com.shishuo.cms.exception.FileNotFoundException;

/**
 * 
 * 文件服务
 * 
 * @author Zhangjiale
 * 
 */
@Service
public class FileService {

	@Autowired
	private FileDao fileDao;

	@Autowired
	private AdminService adminService;

	@Autowired
	private FolderService folderService;

	@Autowired
	private CommentService commentService;

	/**
	 * 得到文件
	 * 
	 * @param fileId
	 * @return File
	 * @throws FileNotFoundException 
	 */

	public FileVo getFileByFileId(long fileId) throws FileNotFoundException {

		FileVo file = fileDao.getFileById(fileId);
		if (file == null) {
			throw new FileNotFoundException(fileId  +" 文件，不存在");
		} else {
			Admin admin = adminService.getAdminById(file.getAdminId());
			file.setAdmin(admin);
			return file;
		}
	}

	/**
	 * 得到目录的所有文件分页
	 * 
	 * @param folderId
	 * @return pageVo
	 */

	public PageVo<FileVo> getFilePageByFolderId(long folderId, int pageNum,
			FileConstant.Type type, int rows) {
		PageVo<FileVo> pageVo = new PageVo<FileVo>(pageNum);
		Folder folder = folderService.getFolderById(folderId);
		if (folder == null) {
			pageVo.setUrl(SystemConstant.BASE_PATH + "/?");
		} else {
			pageVo.setUrl(SystemConstant.BASE_PATH + "/" + folder.getEname()
					+ "?");
		}
		pageVo.setRows(rows);
		pageVo.setCount(this.getFileCountByFolderId(folderId, type));
		List<FileVo> list = this.getAllFileByFolderId(folderId,FileConstant.Status.display,type,
				pageVo.getOffset(), pageVo.getRows());
		pageVo.setList(list);
		return pageVo;
	}

	/**
	 * 得到目录下的所有文件
	 * 
	 * @param foderId
	 * @return
	 */

	public List<FileVo> getAllFileByFolderId(long folderId,FileConstant.Status status,
			FileConstant.Type type, long offset, long rows) {
		List<FileVo> list = fileDao.getFileListByFoderId(folderId, type,
				offset, rows);
		for (FileVo file : list) {
			Admin admin = adminService.getAdminById(file.getAdminId());
			Folder folder = folderService.getFolderById(file.getFolderId());
			file.setAdmin(admin);
			file.setFolder(folder);
			;
		}
		return list;
	}

	/**
	 * 得到目录的某种文件的数量
	 * 
	 * @param folderId
	 * @return Integer
	 */
	public int getFileCountByFolderId(long folderId, FileConstant.Type type) {
		return fileDao.getFileCountByFoderId(folderId, type);
	}

	/**
	 * 增加文件
	 * 
	 * @param folderId
	 * @param adminId
	 * @param picture
	 *            {@link:FileConstant.PICTURE}
	 * @param name
	 * @param content
	 * @param type
	 * @param status
	 * @return
	 */
	public File addFile(long folderId, long adminId,
			FileConstant.Picture picture, String name, String content,
			FileConstant.Type type, FileConstant.Status status) {
		File file = new File();
		file.setFolderId(folderId);
		file.setAdminId(adminId);
		file.setPicture(picture);
		file.setName(name);
		file.setContent(content);
		file.setViewCount(0);
		file.setCommentCount(0);
		file.setType(type);
		file.setStatus(status);
		file.setCreateTime(new Date());
		fileDao.addFile(file);
		return file;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileId
	 * @return boolean
	 */
	public boolean deleteFileByFileId(long fileId) {
		return fileDao.deleteFile(fileId,FileConstant.Status.hidden);
	}

	/**
	 * 修改文件
	 * 
	 * @param fileId
	 * @param folderId
	 * @param adminId
	 * @param picture
	 * @param name
	 * @param content
	 * @param type
	 * @param status
	 * @return
	 */
	public File updateFileByFileId(long fileId, long folderId, long adminId,
			FileConstant.Picture picture, String name, String content,
			FileConstant.Type type, FileConstant.Status status) {
		File file = fileDao.getFileById(fileId);
		file.setFolderId(folderId);
		file.setAdminId(adminId);
		file.setPicture(picture);
		file.setName(name);
		file.setContent(content);
		file.setViewCount(0);
		file.setCommentCount(0);
		file.setType(type);
		file.setStatus(status);
		fileDao.updateFile(file);
		return file;
	}

	/**
	 * 获取某种文件的分页
	 * 
	 * @param type
	 * @param status
	 * @param pageNum
	 * @return PageVo<File>
	 * 
	 */
	public PageVo<File> getAllFileByTypePage(FileConstant.Type type,
			FileConstant.Status status, int pageNum) {
		PageVo<File> pageVo = new PageVo<File>(pageNum);
		pageVo.setRows(5);

		pageVo.setUrl(SystemConstant.BASE_PATH+"/admin/file/page?status="+status+"&type="+type+"&");
		List<File> list = this.getAllFileByType(type,
				status, pageVo.getOffset(), pageVo.getRows());
		pageVo.setList(list);
		pageVo.setCount(this.getAllFileByTypeCount(type,
				status));

		return pageVo;
	}

	/**
	 * 获取不同类型的文件的列表
	 * 
	 * @param type
	 * @param status
	 * @param offset
	 * @param rows
	 * @return List<File>
	 * 
	 */
	public List<File> getAllFileByType(FileConstant.Type type,
			FileConstant.Status status, long offset, long rows) {
		return fileDao.getFileListByType(type, status, offset, rows);
	}

	/**
	 * 获取不同类型的文件的数量
	 * 
	 * @param type
	 * @param status
	 * @param Integer
	 * 
	 */
	public int getAllFileByTypeCount(FileConstant.Type type,
			FileConstant.Status status) {
		return fileDao.getFileListByTypeCount(type, status);
	}

	/**
	 * 放进回收站或者从回收站中还原
	 * @param fileId
	 * @param status
	 * @return boolean
	 * 
	 */
	public void updateStatusByFileId(long fileId, FileConstant.Status status) {
		fileDao.updateStatusByFileId(fileId,status);
	}

	public List<File> getUserImageList(long userId, FileConstant.Type type,
			long offset, long rows) {
		return fileDao.getUserImageList(userId, type, offset, rows);
	}

	public int getUserImageCount(long userId, FileConstant.Type type) {
		return fileDao.getUserImageCount(userId, type);
	}

	public PageVo<File> getUserImagePage(long userId, FileConstant.Type type,
			int pageNum) {
		PageVo<File> pageVo = new PageVo<File>(pageNum);
		pageVo.setRows(20);
		pageVo.setUrl("");
		List<File> list = this.getUserImageList(userId, type,
				pageVo.getOffset(), pageVo.getRows());
		pageVo.setList(list);
		pageVo.setCount(this.getUserImageCount(userId, type));
		return pageVo;
	}

	public int updateImage(long folderId, long fileId, long userId) {
		return fileDao.updateImage(folderId, fileId, userId);
	}

	/**
	 * 更新浏览人数
	 * 
	 * @param fileId
	 * @param viewCount
	 * 
	 */
	public void updateViewCount(long fileId, int nowViewCount) {
		fileDao.updateViewCount(fileId, nowViewCount + 1);
	}

	/**
	 * 更新评论数
	 * 
	 * @param fileId
	 */
	public void updateCommentCount(long fileId) {
		int commentCount = commentService.getCommentCountByFatherId(fileId, 0,
				CommentConstant.Status.display);
		fileDao.updateCommentCount(fileId, commentCount);
	}

	public List<File> getArticleByPicture(FileConstant.Type type,
			FileConstant.Picture picture) {
		return fileDao.getArticleByPicture(type, picture);
	}
}
