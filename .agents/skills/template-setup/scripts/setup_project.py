import os
import sys
import shutil
import re
import argparse

def replace_in_file(file_path, search_pattern, replacement):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = re.sub(search_pattern, replacement, content)
    
    if content != new_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        return True
    return False

def rename_package(root_dir, old_package, new_package):
    print(f"Renaming package from {old_package} to {new_package}...")
    
    # 1. Replace package name in all files
    for root, dirs, files in os.walk(root_dir):
        if '.git' in root or '.gradle' in root or '.idea' in root or 'build' in root:
            continue
        for file in files:
            if file.endswith(('.kt', '.kts', '.xml', '.java', '.properties')):
                file_path = os.path.join(root, file)
                # Replace package declarations and imports
                replace_in_file(file_path, re.escape(old_package), new_package)
                # Also handle directory structure if it's namespace in build.gradle.kts
                replace_in_file(file_path, f'namespace = "{re.escape(old_package)}', f'namespace = "{new_package}')
                replace_in_file(file_path, f'applicationId = "{re.escape(old_package)}', f'applicationId = "{new_package}')

    # 2. Move directories to match new package structure
    old_path_parts = old_package.split('.')
    new_path_parts = new_package.split('.')
    
    for root, dirs, files in os.walk(root_dir):
        if 'src' in root and ('kotlin' in root or 'java' in root):
            # Find the base of the package structure
            parts = root.split(os.sep)
            if 'kotlin' in parts:
                base_idx = parts.index('kotlin')
            elif 'java' in parts:
                base_idx = parts.index('java')
            else:
                continue
                
            package_base = os.sep.join(parts[:base_idx+1])
            old_full_path = os.path.join(package_base, *old_path_parts)
            new_full_path = os.path.join(package_base, *new_path_parts)
            
            if os.path.exists(old_full_path):
                # Ensure new parent directories exist
                os.makedirs(os.path.dirname(new_full_path), exist_ok=True)
                # Move contents
                print(f"Moving {old_full_path} to {new_full_path}")
                # If target directory exists, merge them
                if os.path.exists(new_full_path):
                    for item in os.listdir(old_full_path):
                        s = os.path.join(old_full_path, item)
                        d = os.path.join(new_full_path, item)
                        if os.path.isdir(s):
                            shutil.copytree(s, d, dirs_exist_ok=True)
                            shutil.rmtree(s)
                        else:
                            shutil.move(s, d)
                else:
                    shutil.move(old_full_path, new_full_path)
                
                # Clean up empty old directories
                current = old_full_path
                while current != package_base:
                    if os.path.exists(current) and not os.listdir(current):
                        os.rmdir(current)
                        current = os.path.dirname(current)
                    else:
                        break

def delete_features(root_dir, features):
    print(f"Deleting features: {', '.join(features)}...")
    for feature in features:
        feature_path = os.path.join(root_dir, 'features', feature)
        if os.path.exists(feature_path):
            shutil.rmtree(feature_path)
            print(f"Deleted {feature_path}")
            
    # Update settings.gradle.kts
    settings_path = os.path.join(root_dir, 'settings.gradle.kts')
    if os.path.exists(settings_path):
        with open(settings_path, 'r') as f:
            lines = f.readlines()
        
        new_lines = []
        skip_block = False
        for line in lines:
            # Check if line contains any of the deleted features
            if any(f':features:{feature}' in line for feature in features):
                continue
            new_lines.append(line)
            
        with open(settings_path, 'w') as f:
            f.writelines(new_lines)

def clean_database(root_dir, new_package):
    print("Cleaning database module...")
    db_path = os.path.join(root_dir, 'common/persistence/persistence-database/src/commonMain/kotlin')
    
    # Update AppDatabase.kt
    app_db_found = False
    for root, dirs, files in os.walk(db_path):
        if 'AppDatabase.kt' in files:
            app_db_found = True
            app_db_path = os.path.join(root, 'AppDatabase.kt')
            rel_path = os.path.relpath(root, db_path)
            pkg = rel_path.replace(os.sep, '.')
            with open(app_db_path, 'w') as f:
                f.write(f"package {pkg}\n\n")
                f.write("import androidx.room.ConstructedBy\n")
                f.write("import androidx.room.Database\n")
                f.write("import androidx.room.RoomDatabase\n")
                f.write("import androidx.room.RoomDatabaseConstructor\n\n")
                f.write("@Database(\n")
                f.write("    entities = [],\n")
                f.write("    version = 1,\n")
                f.write(")\n")
                f.write("@ConstructedBy(AppDatabaseConstructor::class)\n")
                f.write("internal abstract class AppDatabase : RoomDatabase()\n\n")
                f.write("@Suppress(\"KotlinNoActualForExpect\")\n")
                f.write("internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {\n")
                f.write("    override fun initialize(): AppDatabase\n")
                f.write("}\n")
        
        if 'PersistenceDatabaseModule.kt' in files:
            module_path = os.path.join(root, 'PersistenceDatabaseModule.kt')
            rel_path = os.path.relpath(root, db_path)
            pkg = rel_path.replace(os.sep, '.')
            # We need to find where DatabaseCreator is
            # Since we renamed everything, it should be under new_package.common.core.persistence.db
            # But rel_path might already include part of new_package if we move directories
            # Let's just assume it's in the same base package
            base_pkg = pkg.rsplit('.di', 1)[0]
            with open(module_path, 'w') as f:
                f.write(f"package {pkg}\n\n")
                f.write("import org.koin.core.module.Module\n")
                f.write("import org.koin.core.module.dsl.singleOf\n")
                f.write("import org.koin.dsl.module\n")
                f.write(f"import {base_pkg}.db.DatabaseCreator\n\n")
                f.write("public val persistenceDatabaseModule: Module = module {\n")
                f.write("    singleOf(::DatabaseCreator)\n")
                f.write("}\n\n")
                f.write("public expect val persistenceDatabasePlatformModule: Module\n\n")
                f.write("internal const val DB_NAME = \"appDatabase\"\n")

    # Delete other files in persistence-database after we've updated the core ones
    for root, dirs, files in os.walk(db_path):
        for file in files:
            if file in ['AppDatabase.kt', 'PersistenceDatabaseModule.kt', 'DatabaseCreator.kt']:
                continue
            if any(x in file for x in ['Weather', 'Coffee', 'Observation']):
                os.remove(os.path.join(root, file))
                print(f"Deleted {file}")

def clean_resources(root_dir):
    print("Cleaning resources...")
    res_path = os.path.join(root_dir, 'common/resources/src/commonMain/composeResources')
    drawable_path = os.path.join(res_path, 'drawable')
    if os.path.exists(drawable_path):
        for file in os.listdir(drawable_path):
            if any(x in file.lower() for x in ['weather', 'wind', 'temperature', 'compass']):
                os.remove(os.path.join(drawable_path, file))
                print(f"Deleted drawable {file}")
                
    # Clean strings.xml (values and values-*)
    for root, dirs, files in os.walk(res_path):
        if 'strings.xml' in files:
            strings_path = os.path.join(root, 'strings.xml')
            with open(strings_path, 'w') as f:
                f.write("""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="error_generic_title">Error</string>
    <string name="error_generic_message">An unknown error occurred. Please try again.</string>
    <string name="error_dialog_retry_button">Retry</string>
    <string name="error_dialog_close_button">Close</string>

    <string name="error_network_title">Connection Error</string>
    <string name="error_network_message">Unable to connect. Please check your internet connection and try again.</string>
</resources>
""")

def main():
    parser = argparse.ArgumentParser(description='Setup KMP template project')
    parser.add_argument('--new-package', type=str, help='New package name')
    parser.add_argument('--new-name', type=str, help='New project name')
    parser.add_argument('--old-package', type=str, default='io.pylyp', help='Old package prefix to replace')
    
    args = parser.parse_args()
    root_dir = os.getcwd()

    if args.new_name:
        print(f"Renaming project to {args.new_name}...")
        settings_path = os.path.join(root_dir, 'settings.gradle.kts')
        if os.path.exists(settings_path):
            replace_in_file(settings_path, r'rootProject\.name = ".*"', f'rootProject.name = "{args.new_name}"')

    current_package = args.old_package
    if args.new_package:
        rename_package(root_dir, args.old_package, args.new_package)
        current_package = args.new_package

    delete_features(root_dir, ['coffee', 'cover', 'weather'])
    clean_database(root_dir, current_package)
    clean_resources(root_dir)
    
    print("Project setup complete!")

if __name__ == "__main__":
    main()
