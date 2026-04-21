variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name used as prefix for all resources"
  type        = string
  default     = "mensageria"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "ecr_image_uri" {
  description = "Full ECR image URI (e.g. 123456789.dkr.ecr.us-east-1.amazonaws.com/mensageria:latest)"
  type        = string
}

variable "db_name" {
  description = "MySQL database name"
  type        = string
  default     = "mensageria"
}

variable "db_username" {
  description = "MySQL master username"
  type        = string
  default     = "mensageria"
}

variable "db_password" {
  description = "MySQL master password"
  type        = string
  sensitive   = true
}

variable "app_port" {
  description = "Port the application listens on"
  type        = number
  default     = 8080
}

variable "app_cpu" {
  description = "ECS task CPU units (256 = 0.25 vCPU)"
  type        = number
  default     = 512
}

variable "app_memory" {
  description = "ECS task memory in MB"
  type        = number
  default     = 1024
}

variable "app_desired_count" {
  description = "Number of ECS task replicas"
  type        = number
  default     = 2
}
