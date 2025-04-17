import md5 from 'md5'

// MD5加密
export const encryptPassword = (password) => {
    return md5(password)
} 